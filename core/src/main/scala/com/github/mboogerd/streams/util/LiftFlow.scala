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

package com.github.mboogerd.streams.util

import akka.stream.scaladsl.{Flow, Keep}

/**
  *
  */
object LiftFlow {

  def contramap[R, S, T, U](flow: Flow[S, T, U])(f: R ⇒ S): Flow[R, T, U] = Flow[R].map(f).viaMat(flow)(Keep.right)

  //  class OptionFlow[S, T, U] extends GraphStage[BidiShape[Option[S], S, T, Option[T]]] {
  //    val in = Inlet[Option[S]]("LiftedFlow.in")
  //    val innerOut = Outlet[S]("LiftedFlow.inner.out")
  //    val innerIn = Inlet[T]("LiftedFlow.inner.in")
  //    val out = Outlet[Option[T]]("LiftedFlow.out")
  //
  //    override def shape: BidiShape[Option[S], S, T, Option[T]] = BidiShape.of(in, innerOut, innerIn, out)
  //
  //    override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) {
  //
  //      var pending: Queue[Int \/ Int] = Queue.empty
  //
  //      def inHandler = new InHandler {
  //        override def onPush(): Unit = {
  //          val elem = grab(in)
  //          elem match {
  //            case Some(s) ⇒
  //              push(innerOut, _)
  //            case None ⇒
  //              queue.enqueue(-\/(None))
  //          }
  //        }
  //      }
  //
  //      def backEndOutHandler = new OutHandler {
  //        @scala.throws[Exception](classOf[Exception])
  //        override def onPull(): Unit = pull(shape.in1)
  //      }
  //
  //      def backEndInHandler = new InHandler {
  //        @scala.throws[Exception](classOf[Exception])
  //        override def onPush(): Unit = ???
  //      }
  //
  //      def frontEndOutHandler = new OutHandler {
  //        override def onPull(): Unit = pull(shape.in2)
  //      }
  //
  //      setHandler(shape.in1, inHandler)
  //      setHandler(shape.out1, backEndOutHandler)
  //      setHandler(shape.in2, backEndInHandler)
  //      setHandler(shape.out2, frontEndOutHandler)
  //    }
  //
  //  }

}
