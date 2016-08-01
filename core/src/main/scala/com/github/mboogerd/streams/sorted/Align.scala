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

import akka.stream._
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import cats.data.Ior
import cats.data.Ior._
import com.github.mboogerd.streams.sorted.Align._

object Align {

  sealed trait Control

  case object EmitLeft extends Control

  case object EmitRight extends Control

  case object EmitBoth extends Control

}

/**
  * Align zips together two streams based on a control function. The function allows one to control inputstream
  * progress, by only progressing a stream of choice, or both streams simultaneously. Backpressure gets
  * propagated accordingly.
  */
class Align[A, B](control: (A, B) ⇒ Align.Control) extends GraphStage[FanInShape2[A, B, A Ior B]] {

  override val shape: FanInShape2[A, B, A Ior B] = new FanInShape2("Align")

  var left = Option.empty[A]
  var right = Option.empty[B]

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) {

    /**
      * Constructs a Ior[A,B] for the given state (left and right buffers, as well as state of the inlets)
      *
      * @param a
      * @param b
      * @return
      */
    def choose(a: Option[A], b: Option[B]): Option[A Ior B] = (left, right) match {
      case (Some(l), Some(r)) => Option(control(l, r) match {
        case EmitLeft => Left(l)
        case EmitRight => Right(r)
        case EmitBoth => Both(l, r)
      })
      case (Some(l), None) if isClosed(shape.in1) => Option(Left(l))
      case (None, Some(r)) if isClosed(shape.in0) => Option(Right(r))
      case _ => None
    }

    /**
      * pushes the desired element (if any) to out, and clears the buffers correspondingly
      */
    def pushIt(): Unit = {
      choose(left, right) foreach {
        case l@Left(_) ⇒
          push(shape.out, l)
          left = None
        case b@Both(_, _) ⇒
          push(shape.out, b)
          left = None
          right = None
        case r@Right(_) ⇒
          push(shape.out, r)
          right = None
      }
    }

    /**
      * Moves an element from the inlets to their respective buffer and initiates its dispatch.
      * If all inlets are closed, and no buffer remains, it completes this stage.
      */
    def check(): Unit = {
      if (left.isEmpty && isAvailable(shape.in0)) left = Some(grab(shape.in0))
      if (right.isEmpty && isAvailable(shape.in1)) right = Some(grab(shape.in1))
      if (isAvailable(shape.out)) pushIt()

      if (isClosed(shape.in0)
        && isClosed(shape.in1)
        && left.isEmpty
        && right.isEmpty) {
        completeStage()
      }
    }

    def inHandler = new InHandler {
      override def onPush(): Unit = check()

      override def onUpstreamFinish(): Unit = check()
    }

    setHandler(shape.in0, inHandler)
    setHandler(shape.in1, inHandler)

    setHandler(shape.out, new OutHandler {
      override def onPull(): Unit = {
        if (left.isEmpty)
          tryPull(shape.in0)

        if (right.isEmpty)
          tryPull(shape.in1)

        check()
      }
    })
  }
}
