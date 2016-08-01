/*
 * Copyright 2015 Merlijn Boogerd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mboogerd.streams.sorted

import akka.NotUsed
import akka.stream.ActorAttributes.SupervisionStrategy
import akka.stream._
import akka.stream.scaladsl.Source
import akka.stream.stage._

import scala.concurrent.duration.{FiniteDuration, _}
import scala.util.control.NonFatal

/**
  * A group-by specifically written for streams that are contiguous w.r.t. the computed key. Sorted-stream is a sufficient,
  * but not a necessary condition for this to work. Informally, what matters is that all elements that map to the same
  * key form groups of consecutive elements, i.e:
  *
  * for-all s, t in inputstream, such that s precedes t directly: if `keyFor(s)` != `keyFor(t)`, then:
  * - there exists no element u in inputstream such that t precedes u (at any distance) s.t. `keyFor(s)` == `keyFor(u)`; and,
  * - there exists no element r in inputstream such that r precedes s (at any distance) s.t. `keyFor(r)` == `keyFor(t)`.
  *
  * All groups of stream-elements are propagated to their own substream. What distinguishes this implementation from
  * the typical group-by implementation is that sub-streams can be closed directly after witnessing a value that maps
  * to a different key. The traditional group-by must keep all sub-streams open until the superstream is consumed, as
  * only then it can guarantee that no more elements will be propagated to any of the sub-streams. Resources can be
  * freed immediately, given the guarantee that no more elements will arrive for the substream.
  *
  * An contiguous approach (if the stream permits it) allows you to stay closer to business-semantics; There is no
  * need to specify how many groups may be processed concurrently. Also, we have more (but not complete) freedom to wait
  * in our superstream for completion of a substream, without introducing deadlocks. Both these permit us to use
  * a declarative group-by, where otherwise this logic would be obfuscated by stream-management.
  */
final class ContiguousGroupBy[T, K](keyFor: T ⇒ K) extends GraphStage[FlowShape[T, (K, Source[T, NotUsed])]] {

  val maxSubstreams: Int = 1
  val in: Inlet[T] = Inlet("ContiguousGroupBy.in")
  val out: Outlet[(K, Source[T, NotUsed])] = Outlet("ContiguousGroupBy.out")
  override val shape: FlowShape[T, (K, Source[T, NotUsed])] = FlowShape(in, out)

  override def initialAttributes = Attributes.name("ContiguousGroupBy")

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new TimerGraphStageLogic(shape) with OutHandler with InHandler {
    parent ⇒
    lazy val decider = inheritedAttributes.get[SupervisionStrategy].map(_.decider).getOrElse(Supervision.stoppingDecider)

    private var activeElementKey: K = null.asInstanceOf[K]
    private var activeSubstream: Option[SubstreamSource] = None
    private var completeSubstreams = new java.util.HashSet[Any]()
    private val closedSubstreams = new java.util.HashSet[Any]()

    private var timeout: FiniteDuration = 5.seconds
    //FIXME
    private var substreamWaitingToBePushed: Option[SubstreamSource] = None
    private var nextElementKey: K = null.asInstanceOf[K]
    private var nextElementValue: T = null.asInstanceOf[T]
    private var _nextId = 0
    private val substreamsJustStarted = new java.util.HashSet[Any]()
    private var firstPushCounter: Int = 0

    private def nextId(): Long = {
      _nextId += 1; _nextId
    }

    private def hasNextElement = nextElementKey != null

    private def clearNextElement(): Unit = {
      nextElementKey = null.asInstanceOf[K]
      nextElementValue = null.asInstanceOf[T]
    }

    private def tryCompleteAll(): Boolean =
      if (activeSubstream.isEmpty || (!hasNextElement && firstPushCounter == 0)) {
        activeSubstream.foreach(_.complete())
        completeStage()
        true
      } else false

    private def fail(ex: Throwable): Unit = {
      activeSubstream.foreach(_.fail(ex))
      failStage(ex)
    }

    private def needToPull: Boolean = !(hasBeenPulled(in) || isClosed(in) || hasNextElement)

    override def onPull(): Unit = {
      substreamWaitingToBePushed match {
        // either push an earlier prepared substream
        case Some(substreamSource) ⇒
          push(out, activeElementKey → Source.fromGraph(substreamSource.source))
          scheduleOnce(substreamSource.key, timeout)
          substreamWaitingToBePushed = None
        // or add a cached element to the active one, and indicate demand
        case None ⇒
          if (hasNextElement) {
            val subSubstreamSource = activeSubstream.get // from original source, this seems to be safe in this state...
            if (subSubstreamSource.isAvailable) {
              subSubstreamSource.push(nextElementValue)
              clearNextElement()
            }
          } else tryPull(in)
      }
    }

    override def onUpstreamFailure(ex: Throwable): Unit = fail(ex)

    override def onDownstreamFinish(): Unit =
      if (activeSubstream.isEmpty) completeStage() else setKeepGoing(true)

    override def onPush(): Unit = try {
      val elem = grab(in)
      val previous = activeElementKey
      activeElementKey = keyFor(elem)
      require(activeElementKey != null, "Key cannot be null")

      val substreamSource = activeSubstream.orNull
      if (activeElementKey == previous && substreamSource != null) {
        if (substreamSource.isAvailable) substreamSource.push(elem)
        else {
          nextElementKey = activeElementKey
          nextElementValue = elem
        }
      } else {
        if (closedSubstreams.contains(activeElementKey) && !hasBeenPulled(in))
          pull(in)
        else
          runSubstream(activeElementKey, elem)
      }

    } catch {
      case NonFatal(ex) ⇒
        decider(ex) match {
          case Supervision.Stop ⇒ fail(ex)
          case Supervision.Resume | Supervision.Restart ⇒ if (!hasBeenPulled(in)) pull(in)
        }
    }

    override def onUpstreamFinish(): Unit = {
      if (!tryCompleteAll()) setKeepGoing(true)
    }

    private def runSubstream(key: K, value: T): Unit = {
      val substreamSource = new SubstreamSource("SortedStreamGroupBySource " + nextId, key, value)

      //ADDED: Once a new stream is initialized, the old stream may be closed, if one exists
      activeSubstream.foreach { stream =>
        if (firstPushCounter == 0 && nextElementKey != key) {
          stream.complete()
          closedSubstreams.add(stream)
        } else {
          completeSubstreams.add(stream)
        }
      }

      activeSubstream = Some(substreamSource)
      firstPushCounter += 1
      if (isAvailable(out)) {
        push(out, activeElementKey → Source.fromGraph(substreamSource.source))
        scheduleOnce(key, timeout)
        substreamWaitingToBePushed = None
      } else {
        setKeepGoing(true)
        substreamsJustStarted.add(substreamSource)
        substreamWaitingToBePushed = Some(substreamSource)
      }
    }

    override protected def onTimer(timerKey: Any): Unit = {
      val substreamSource = activeSubstream.orNull
      if (substreamSource != null) {
        substreamSource.timeout(timeout)
        closedSubstreams.add(timerKey)
        activeSubstream = None
        if (isClosed(in)) tryCompleteAll()
      }
    }

    setHandlers(in, out, this)

    private class SubstreamSource(name: String, val key: K, var firstElement: T) extends SubSourceOutlet[T](name) with OutHandler {
      def firstPush(): Boolean = firstElement != null

      def hasNextForSubSource = hasNextElement && nextElementKey == key

      private def completeSubStream(): Unit = {
        complete()
        if (activeSubstream.contains(this))
          activeSubstream = None
        closedSubstreams.add(key)
      }

      private def tryCompleteHandler(): Unit = {
        if ((parent.isClosed(in) || completeSubstreams.contains(this)) && !hasNextForSubSource) {
          completeSubStream()
          tryCompleteAll()
        }
      }

      override def onPull(): Unit = {
        cancelTimer(key)
        if (firstPush()) {
          firstPushCounter -= 1
          push(firstElement)
          firstElement = null.asInstanceOf[T]
          substreamsJustStarted.remove(this)
          if (substreamsJustStarted.isEmpty) setKeepGoing(false)
        } else if (hasNextForSubSource) {
          push(nextElementValue)
          clearNextElement()
        } else if (needToPull) pull(in)

        tryCompleteHandler()
      }

      override def onDownstreamFinish(): Unit = {
        if (hasNextElement && nextElementKey == key) clearNextElement()
        if (firstPush()) firstPushCounter -= 1
        completeSubStream()
        if (parent.isClosed(in)) tryCompleteAll() else if (needToPull) pull(in)
      }


      setHandler(this)
    }

  }

  override def toString: String = "ContiguousGroupBy"

}
