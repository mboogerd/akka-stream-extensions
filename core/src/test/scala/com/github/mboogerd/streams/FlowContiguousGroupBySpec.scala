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

package com.github.mboogerd.streams

import akka.NotUsed
import akka.stream.Supervision.resumingDecider
import akka.stream.scaladsl.{RunnableGraph, Sink, Source, SubFlow}
import akka.stream.testkit.TestSubscriber.ManualProbe
import akka.stream.testkit.Utils._
import akka.stream.testkit.scaladsl.TestSink
import akka.stream.testkit.{TestSubscriber, _}
import akka.stream.{ActorAttributes, ActorMaterializer, ActorMaterializerSettings}
import akka.util.ByteString
import org.reactivestreams.Publisher
import org.scalacheck.Arbitrary._
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor1}
import com.github.mboogerd.streams.GenUtils._
import com.github.mboogerd.streams.sorted.SortedStreamUtil._

import scala.collection.immutable.Seq
import scala.concurrent.duration.{FiniteDuration, _}
import scala.concurrent.forkjoin.ThreadLocalRandom
import scala.concurrent.{Await, Future}

/**
  * This test is constructed from the Akka 2.4.7 FlowGroupBySpec, and adjusted for our implementation of contiguous-
  * group-by, which assumes that elements in the stream are pre-grouped.
  */
object FlowContiguousGroupBySpec {

  implicit class Lift[M](val f: SubFlow[Int, M, Source[Int, M]#Repr, RunnableGraph[M]]) extends AnyVal {
    def lift(key: Int ⇒ Int) = f.prefixAndTail(1).map(p ⇒ key(p._1.head) → (Source.single(p._1.head) ++ p._2)).concatSubstreams
  }

}

class FlowContiguousGroupBySpec extends AkkaSpec with TableDrivenPropertyChecks with ScalaFutures {

  val settings = ActorMaterializerSettings(system)
    .withInputBuffer(initialSize = 2, maxSize = 2)

  implicit val materializer = ActorMaterializer(settings)

  def randomByteString(size: Int): ByteString = {
    val a = new Array[Byte](size)
    ThreadLocalRandom.current().nextBytes(a)
    ByteString(a)
  }

  case class StreamPuppet(p: Publisher[Int]) {
    val probe = TestSubscriber.manualProbe[Int]()
    p.subscribe(probe)
    val subscription = probe.expectSubscription()

    def request(demand: Int): Unit = subscription.request(demand)

    def expectNext(elem: Int): Unit = probe.expectNext(elem)

    def expectNoMsg(max: FiniteDuration): Unit = probe.expectNoMsg(max)

    def expectComplete(): Unit = probe.expectComplete()

    def expectError(e: Throwable) = probe.expectError(e)

    def cancel(): Unit = subscription.cancel()
  }

  class SubstreamsSupport(groupCount: Int = 2, elementCount: Int = 2, maxSubstreams: Int = -1) {

    private val source: Source[Int, NotUsed] = Source(0 until (groupCount * elementCount))
    val sourcePublisher = source.runWith(Sink.asPublisher(false))

    val groupStream = Source.fromPublisher(sourcePublisher).contiguousGroupBy(_ / elementCount + 1).runWith(Sink.asPublisher(false))
    val masterSubscriber = TestSubscriber.manualProbe[(Int, Source[Int, _])]()

    groupStream.subscribe(masterSubscriber)
    val masterSubscription = masterSubscriber.expectSubscription()

    def getSubFlow(expectedKey: Int): Source[Int, _] = {
      masterSubscription.request(1)
      expectSubFlow(expectedKey)
    }

    def expectSubFlow(expectedKey: Int): Source[Int, _] = {
      val (key, substream) = masterSubscriber.expectNext()
      key should be(expectedKey)
      substream
    }
  }

  "groupBy" must {
    "work in the happy case" in assertAllStagesStopped {
      new SubstreamsSupport(groupCount = 2, elementCount = 2) {
        val s1 = StreamPuppet(getSubFlow(1).runWith(Sink.asPublisher(false)))
        masterSubscriber.expectNoMsg(100.millis)

        // first element should be delivered on demand
        s1.expectNoMsg(100.millis)
        s1.request(1)
        s1.expectNext(0)
        s1.expectNoMsg(100.millis)

        // second element too
        s1.expectNoMsg(100.millis)
        s1.request(1)
        s1.expectNext(1)
        s1.expectNoMsg(100.millis)

        s1.request(1)
        s1.expectComplete()

        val s2 = StreamPuppet(getSubFlow(2).runWith(Sink.asPublisher(false)))

        s2.expectNoMsg(100.millis)
        s2.request(2)
        s2.expectNext(2)
        s2.expectNext(3)
        s2.expectComplete()

        masterSubscription.request(1)
        masterSubscriber.expectComplete()
      }
    }

    "work in normal user scenario" in {
      Source(List("Aaa", "Abb", "Bcc", "Cdd", "Cee"))
        .contiguousGroupBy(_.substring(0, 1))
        .map { case (key, src) ⇒ src.grouped(10) }
        .flatMapConcat(identity)
        .grouped(10)
        .runWith(Sink.head)
        .futureValue(Timeout(3.seconds))
        .sortBy(_.head) should ===(List(List("Aaa", "Abb"), List("Bcc"), List("Cdd", "Cee")))
    }

    "fail when key function return null" in {
      val down = Source(List("Aaa", "Abb", "Bcc", "Cdd", "Cee"))
        .contiguousGroupBy(e ⇒ if (e.startsWith("A")) null else e.substring(0, 1))
        .flatMapConcat(_._2)
        .grouped(10)
        .runWith(TestSink.probe[Seq[String]])
      down.request(1)
      val ex = down.expectError()
      ex.getMessage.indexOf("Key cannot be null") should not be (-1)
      ex.isInstanceOf[IllegalArgumentException] should be(true)
    }

    "accept cancellation of substreams" in assertAllStagesStopped {
      new SubstreamsSupport(groupCount = 2) {
        StreamPuppet(getSubFlow(1).runWith(Sink.asPublisher(false))).cancel()

        val substream = StreamPuppet(getSubFlow(2).runWith(Sink.asPublisher(false)))
        substream.request(2)
        substream.expectNext(2)
        substream.expectNext(3)
        substream.expectComplete()

        masterSubscription.request(1)
        masterSubscriber.expectComplete()
      }
    }

    "accept cancellation of master stream when not consumed anything" in assertAllStagesStopped {
      val publisherProbeProbe = TestPublisher.manualProbe[Int]()
      val publisher: Publisher[(Int, Source[Int, NotUsed])] = Source.fromPublisher(publisherProbeProbe).contiguousGroupBy(_ % 2).runWith(Sink.asPublisher(false))
      val subscriber: ManualProbe[(Int, Source[Int, _])] = TestSubscriber.manualProbe[(Int, Source[Int, _])]()
      publisher.subscribe(subscriber)

      val upstreamSubscription = publisherProbeProbe.expectSubscription()
      val downstreamSubscription = subscriber.expectSubscription()
      downstreamSubscription.cancel()
      upstreamSubscription.expectCancellation()
    }

    "work with empty input stream" in assertAllStagesStopped {
      val publisher = Source(List.empty[Int]).contiguousGroupBy(_ % 2).runWith(Sink.asPublisher(false))
      val subscriber = TestSubscriber.manualProbe[(Int, Source[Int, _])]()
      publisher.subscribe(subscriber)

      subscriber.expectSubscriptionAndComplete()
    }

    "abort on onError from upstream" in assertAllStagesStopped {
      val publisherProbeProbe = TestPublisher.manualProbe[Int]()
      val publisher = Source.fromPublisher(publisherProbeProbe).contiguousGroupBy(_ % 2).runWith(Sink.asPublisher(false))
      val subscriber = TestSubscriber.manualProbe[(Int, Source[Int, _])]()
      publisher.subscribe(subscriber)

      val upstreamSubscription = publisherProbeProbe.expectSubscription()

      val downstreamSubscription = subscriber.expectSubscription()
      downstreamSubscription.request(100)

      val e = TE("test")
      upstreamSubscription.sendError(e)

      subscriber.expectError(e)
    }

    "abort on onError from upstream when substreams are running" in assertAllStagesStopped {
      val publisherProbeProbe = TestPublisher.manualProbe[Int]()
      val publisher = Source.fromPublisher(publisherProbeProbe).contiguousGroupBy(_ % 2).runWith(Sink.asPublisher(false))
      val subscriber = TestSubscriber.manualProbe[(Int, Source[Int, _])]()
      publisher.subscribe(subscriber)

      val upstreamSubscription = publisherProbeProbe.expectSubscription()

      val downstreamSubscription = subscriber.expectSubscription()
      downstreamSubscription.request(100)

      upstreamSubscription.sendNext(1)

      val (_, substream) = subscriber.expectNext()
      val substreamPuppet = StreamPuppet(substream.runWith(Sink.asPublisher(false)))

      substreamPuppet.request(1)
      substreamPuppet.expectNext(1)

      val e = TE("test")
      upstreamSubscription.sendError(e)

      substreamPuppet.expectError(e)
      subscriber.expectError(e)

    }

    "fail stream when groupBy function throws" in assertAllStagesStopped {
      val publisherProbeProbe = TestPublisher.manualProbe[Int]()
      val exc = TE("test")
      val publisher = Source.fromPublisher(publisherProbeProbe)
        .contiguousGroupBy(elem ⇒ if (elem == 2) throw exc else elem % 2)
        .runWith(Sink.asPublisher(false))
      val subscriber = TestSubscriber.manualProbe[(Int, Source[Int, NotUsed])]()
      publisher.subscribe(subscriber)

      val upstreamSubscription = publisherProbeProbe.expectSubscription()

      val downstreamSubscription = subscriber.expectSubscription()
      downstreamSubscription.request(100)

      upstreamSubscription.sendNext(1)

      val (_, substream) = subscriber.expectNext()
      val substreamPuppet = StreamPuppet(substream.runWith(Sink.asPublisher(false)))

      substreamPuppet.request(1)
      substreamPuppet.expectNext(1)

      upstreamSubscription.sendNext(2)

      subscriber.expectError(exc)
      substreamPuppet.expectError(exc)
      upstreamSubscription.expectCancellation()
    }

    "resume stream when groupBy function throws" in {
      val publisherProbeProbe = TestPublisher.manualProbe[Int]()
      val exc = TE("test")
      val publisher: Publisher[(Int, Source[Int, NotUsed])] = Source.fromPublisher(publisherProbeProbe)
        .contiguousGroupBy(elem ⇒ if (elem == 2) throw exc else elem % 2)
        .withAttributes(ActorAttributes.supervisionStrategy(resumingDecider))
        .runWith(Sink.asPublisher(false))
      val subscriber = TestSubscriber.manualProbe[(Int, Source[Int, NotUsed])]()
      publisher.subscribe(subscriber)

      val upstreamSubscription = publisherProbeProbe.expectSubscription()

      val downstreamSubscription = subscriber.expectSubscription()
      downstreamSubscription.request(100)

      upstreamSubscription.sendNext(1)
      upstreamSubscription.sendNext(5)
      upstreamSubscription.sendNext(4)

      val (_, substream1) = subscriber.expectNext()
      val substreamPuppet1 = StreamPuppet(substream1.runWith(Sink.asPublisher(false)))
      substreamPuppet1.request(10)
      substreamPuppet1.expectNext(1)
      substreamPuppet1.expectNext(5)
      substreamPuppet1.expectComplete()

      upstreamSubscription.sendNext(2)
      upstreamSubscription.sendNext(6)


      val (_, substream2) = subscriber.expectNext()
      val substreamPuppet2 = StreamPuppet(substream2.runWith(Sink.asPublisher(false)))
      substreamPuppet2.request(10)
      substreamPuppet2.expectNext(4)
      substreamPuppet2.expectNext(6) // note that 2 was dropped

      upstreamSubscription.sendComplete()
      subscriber.expectComplete()
      substreamPuppet2.expectComplete()
    }

    "pass along early cancellation" in assertAllStagesStopped {
      val up = TestPublisher.manualProbe[Int]()
      val down = TestSubscriber.manualProbe[(Int, Source[Int, NotUsed])]()

      val flowSubscriber = Source.asSubscriber[Int].contiguousGroupBy(_ % 2).to(Sink.fromSubscriber(down)).run()

      val downstream = down.expectSubscription()
      downstream.cancel()
      up.subscribe(flowSubscriber)
      val upsub = up.expectSubscription()
      upsub.expectCancellation()
    }

    "emit subscribe before completed" in assertAllStagesStopped {
      val futureGroupSource: Future[Source[Int, NotUsed]] =
        Source.single(0)
          .contiguousGroupBy(elem ⇒ "all")
          .map(_._2.prefixAndTail(0))
          .flatMapConcat(identity)
          .map(_._2)
          .runWith(Sink.head)

      val pub: Publisher[Int] = Await.result(futureGroupSource, 3.seconds).runWith(Sink.asPublisher(false))
      val probe = TestSubscriber.manualProbe[Int]()
      pub.subscribe(probe)
      val sub = probe.expectSubscription()
      sub.request(1)
      probe.expectNext(0)
      probe.expectComplete()

    }

    "work under fuzzing stress test" in assertAllStagesStopped {
      val publisherProbe = TestPublisher.manualProbe[ByteString]()
      val subscriber = TestSubscriber.manualProbe[ByteString]()

      val publisher: Publisher[ByteString] = Source.fromPublisher[ByteString](publisherProbe)
        .contiguousGroupBy(elem ⇒ elem.head).map(_._2.map(_.reverse)).flatMapMerge(3, identity)
        .contiguousGroupBy(elem ⇒ elem.head).map(_._2.map(_.reverse)).flatMapMerge(3, identity)
        .runWith(Sink.asPublisher(false))

      publisher.subscribe(subscriber)

      val upstreamSubscription = publisherProbe.expectSubscription()
      val downstreamSubscription = subscriber.expectSubscription()

      downstreamSubscription.request(300)
      for (i ← 1 to 300) {
        val byteString = randomByteString(10)
        upstreamSubscription.expectRequest()
        upstreamSubscription.sendNext(byteString)
        subscriber.expectNext() should ===(byteString)
      }
      upstreamSubscription.sendComplete()

    }


    "not block on the major stream waiting for the substream" in assertAllStagesStopped {

      val test1 = Source(List("Aaa", "Abb", "Bcc", "Cdd", "Cee"))
        .contiguousGroupBy(_.substring(0, 1))
        .mapAsync(1) { case (_, source) ⇒ source.runWith(Sink.seq) }
        .runWith(Sink.seq)


      val test2 = Source(List(1, 1, 2, 3, 3))
        .contiguousGroupBy(identity)
        .mapAsync(2) { case (_, source) ⇒ source.runWith(Sink.seq) }
        .runWith(Sink.seq)


      test1.futureValue should equal(Seq(Seq("Aaa", "Abb"), Seq("Bcc"), Seq("Cdd", "Cee")))
      test2.futureValue should equal(Seq(Seq(1, 1), Seq(2), Seq(3, 3)))
    }


    "produce equivalent results to groupBy for pre-grouped streams" in assertAllStagesStopped {

      def uniqueInts = arbitrary[Int].distinct
      def groupSize = arbitrary[Int].map(i ⇒ i.min(4).max(1))
      def testDataSize = arbitrary[Int].map(i ⇒ i.min(4).max(0))
      def groupedInts = uniqueInts.zip(groupSize.toStream).flatMap { case (i, l) ⇒ Stream.continually(i).take(l) }
      def testData = testDataSize.toStream.zip(Stream.continually(groupedInts)).map { case (l, g) ⇒ g.take(l) }

      val timeout: Timeout = Timeout(10.seconds)

      forAll(new TableFor1("int-groups", testData.take(250): _*)) { seq ⇒
        val source = Source(seq)

        val comparison: Future[Seq[Seq[Int]]] = source
          .groupBy(Int.MaxValue, identity)
          .fold(Vector.empty[Int])(_ :+ _)
          .mergeSubstreams
          .runWith(Sink.seq)

        // FIXME: One would expect this to work, but it will occasionally deadlock...
        // Observations: When Future.sequence is invoked before demand has been indicated from one of the substreams,
        // it seems that no items will ever be requested from that substream, which therefore never completes the
        // substream and subsequently the stream stops making progress
        //        val test: Future[Seq[Seq[Int]]] = source
        //          .contiguousGroupBy(identity)
        //          .map(_._2.runWith(Sink.seq))
        //          .runWith(Sink.seq)
        //          .flatMap(Future.sequence(_))

        val test: Future[Seq[Seq[Int]]] = source
          .contiguousGroupBy(identity)
          .mapAsync(1){ _._2.runWith(Sink.seq)}
          .runWith(Sink.seq)

        val testResult: Seq[Seq[Int]] = test.futureValue(timeout).sortBy(_.head)
        val comparisonResult: Seq[Seq[Int]] = comparison.futureValue(timeout).sortBy(_.head)

        testResult should equal(comparisonResult)
      }
    }


    "also work correctly if downstream from substream has not consumed all its elements when a new key arrives" in assertAllStagesStopped {
      val publisherProbeProbe = TestPublisher.manualProbe[Int]()
      val publisher: Publisher[(Int, Source[Int, NotUsed])] = Source.fromPublisher(publisherProbeProbe).contiguousGroupBy(identity).runWith(Sink.asPublisher(false))
      val subscriber: ManualProbe[(Int, Source[Int, _])] = TestSubscriber.manualProbe[(Int, Source[Int, _])]()

      publisher.subscribe(subscriber)

      val upstreamSubscription = publisherProbeProbe.expectSubscription()

      val downstreamSubscription = subscriber.expectSubscription()
      downstreamSubscription.request(100)

      upstreamSubscription.sendNext(1)

      upstreamSubscription.sendNext(2)
      upstreamSubscription.sendNext(2)

      val (_, substream1) = subscriber.expectNext()
      val substreamPuppet1 = StreamPuppet(substream1.runWith(Sink.asPublisher(false)))
      substreamPuppet1.request(10)
      substreamPuppet1.expectNext(1) // gets Completion if upstream.sendNext precedes
      substreamPuppet1.expectComplete()

      val (_, substream2) = subscriber.expectNext()
      val substreamPuppet2 = StreamPuppet(substream2.runWith(Sink.asPublisher(false)))

      substreamPuppet2.request(10)
      substreamPuppet2.expectNext(2)
      substreamPuppet2.expectNext(2)

      upstreamSubscription.sendComplete()

      substreamPuppet2.expectComplete()
    }
  }
}
