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

import akka.stream.scaladsl.{Flow, GraphDSL, Keep, Source}
import akka.stream.{FlowShape, SourceShape}
import cats._
import cats.data.Ior
import com.github.mboogerd.streams.sorted.Align.{EmitBoth, EmitLeft, EmitRight}

/**
  *
  */
trait AlignedStreamOpsPrio2 {

  def controlFromPartialOrder[T: PartialOrder]: (T, T) ⇒ Align.Control = { (t1, t2) ⇒
    implicitly[PartialOrder[T]].partialCompare(t1, t2) match {
      case x if x < 0 ⇒ EmitLeft
      case x if x > 0 ⇒ EmitRight
      case _ ⇒ EmitBoth
    }
  }

  implicit class SourceWithAlignment[S, T](sourceS: Source[S, T]) {
    def align[V](sourceS2: Source[S, V])(implicit orderS: PartialOrder[S]): Source[Ior[S, S], T] = {
      align(sourceS2, controlFromPartialOrder)
    }

    def align[U, V](sourceU: Source[U, V], control: (S, U) ⇒ Align.Control): Source[Ior[S, U], T] = {
      Source.fromGraph(GraphDSL.create(sourceS) { implicit builder ⇒ streamS ⇒
        import GraphDSL.Implicits._
        val selector = builder.add(new Align[S, U](control))
        streamS ~> selector.in0
        sourceU ~> selector.in1

        SourceShape(selector.out)
      })
    }
  }

  implicit class FlowWithAlignment[S, T, X](flowT: Flow[S, T, X]) {

    def align[V](sourceT: Source[T, V])(implicit orderT: PartialOrder[T]): Flow[S, Ior[T, T], X] = {
      align(sourceT, controlFromPartialOrder)
    }

    def align[U, V](sourceU: Source[U, V], control: (T, U) ⇒ Align.Control): Flow[S, Ior[T, U], X] = {
      Flow.fromGraph(GraphDSL.create(flowT) { implicit builder ⇒ flowT ⇒
        import GraphDSL.Implicits._
        val selector = builder.add(new Align[T, U](control))
        flowT ~> selector.in0
        sourceU ~> selector.in1

        FlowShape(flowT.in, selector.out)
      })
    }
  }

}

trait AlignedStreamsOps extends AlignedStreamOpsPrio2 {

  /**
    * Allows alignment of two key-value (tuple2) streams that share the key type, into a stream of keys that map to
    * an inclusive-or of values from either side. Alignment is controlled by a _total_ order on the key, as opposed to
    * partial orders which we use elsewhere. The reason for this is that we want to provide the convenience of having
    * only one key instance in the output stream. If only a partial order was provided for the key, we would have to
    * arbitrarily chose one of the keys when pairs on both sides were propagated with incomparable keys.
    */
  implicit class KeyValueSourceWithAlignment[K: Order, V1, M1](sourceKV1: Source[(K, V1), M1]) {

    def alignKeys[V2, M2](sourceKV2: Source[(K, V2), M2]): Source[(K, Ior[V1, V2]), M1] =
      alignKeysMat(sourceKV2)(Keep.left)

    def alignKeysMat[V2, M2, Mat](sourceKV2: Source[(K, V2), M2])(combineMat: (M1, M2) ⇒ Mat): Source[(K, Ior[V1, V2]), Mat] = {
      // lifts the key-control function to one that considers key-value pairs (and proceeds by ignoring that value)
      val keyValueControl: ((K, V1), (K, V2)) ⇒ Align.Control = {
        case ((k1, _), (k2, _)) ⇒ controlFromPartialOrder(implicitly[Order[K]])(k1, k2)
      }

      Source.fromGraph(GraphDSL.create(sourceKV1, sourceKV2)(combineMat) { implicit builder ⇒ (kv1, kv2) ⇒
        import GraphDSL.Implicits._
        val selector = builder.add(new Align[(K, V1), (K, V2)](keyValueControl))
        val eliminateRedundantKey = builder.add(Flow[Ior[(K, V1), (K, V2)]].map {
          case Ior.Left((k, v)) ⇒ k → Ior.left(v)
          case Ior.Right((k, v)) ⇒ k → Ior.right(v)
          // Given that we use a total order for alignment-control on key, we may assume keys are equivalent!
          case Ior.Both((k, v1), (_, v2)) ⇒ k → Ior.both(v1, v2)
        })

        kv1 ~> selector.in0
        kv2 ~> selector.in1

        selector.out ~> eliminateRedundantKey.in

        SourceShape(eliminateRedundantKey.out)
      })
    }
  }

}

object AlignedStreamsOps extends AlignedStreamsOps
