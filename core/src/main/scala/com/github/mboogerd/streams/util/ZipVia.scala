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

import akka.stream.FlowShape
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Keep, Source, Zip}

/**
  *
  */
object ZipVia {


  def mapZip[S, T, V](flow: Flow[S, T, V]): Flow[S, (S, T), V] = Flow.fromGraph(
    GraphDSL.create(flow) { implicit b ⇒ f ⇒
      import GraphDSL.Implicits._

      val broadcast = b.add(Broadcast[S](2))
      val zip = b.add(Zip[S, T]())

      // connect the graph
      broadcast.out(0) ~> zip.in0
      broadcast.out(1) ~> f ~> zip.in1

      FlowShape(broadcast.in, zip.out)
    })


  final class ZipViaSourceOps[S, U](source: Source[S, U]) {

    def zipVia[T, V](flow: Flow[S, T, V]): Source[(S, T), U] = zipViaMat(flow)(Keep.left)

    /**
      * Forks the given source and maps the supplied flow over it, then rejoins the two paths into a single path of tuples,
      * preserving the original input value, enriched using the mapped flow.
      */
    def zipViaMat[T, V, Mat](flow: Flow[S, T, V])(comb: (U, V) => Mat): Source[(S, T), Mat] = {
      source.viaMat(mapZip(flow))(comb)
    }
  }

  final class ZipViaFlowOps[R, S, U](flow1: Flow[R, S, U]) {

    def zipVia[T, V](flow: Flow[S, T, V]): Flow[R, (S, T), U] = zipViaMat(flow)(Keep.left)

    /**
      * Forks `flow§` and maps `flow2` over it, then rejoins the two paths into a single path of tuples,
      * preserving the original input value, enriched using the mapped flow.
      */
    def zipViaMat[T, V, Mat](flow2: Flow[S, T, V])(comb: (U, V) => Mat): Flow[R, (S, T), Mat] = {
      flow1.viaMat(mapZip(flow2))(comb)
    }
  }

}
