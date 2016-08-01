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

import akka.NotUsed
import akka.stream.FlowShape
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge, Source}

/**
  *
  */
object Route {

  def routeUnorderedFlow[S, T](routingTable: RoutingTable[S, T]): Flow[S, T, NotUsed] = {

    Flow.fromGraph(GraphDSL.create() { implicit builder ⇒
      import GraphDSL.Implicits._

      val bcast = builder.add(Broadcast[S](routingTable.entries.size))
      val merge = builder.add(Merge[T](routingTable.entries.size))

      routingTable.entries.foreach { case RoutingEntry(flow, condition) ⇒
        val filter = builder.add(Flow[S].filter(condition))

        bcast ~> filter ~> flow ~> merge
      }

      FlowShape(bcast.in, merge.out)
    })
  }

  case class RoutingEntry[-S, +T](to: Flow[S, T, NotUsed], when: S ⇒ Boolean)

  case class RoutingTable[-S, +T](entries: RoutingEntry[S, T]*)

  final class RouteSourceOps[S, U](source: Source[S, U]) {
    //    def route[T](routingTable: RoutingTable[S, T]): Source[T, U] = source.via(routeFlow(routingTable))
    def routeUnordered[T](routingTable: RoutingTable[S, T]): Source[T, U] = source.via(routeUnorderedFlow(routingTable))
  }

  final class RouteFlowOps[R, S, U](flow: Flow[R, S, U]) {
    //    def route[T](routingTable: RoutingTable[S, T]): Flow[R, T, U] = flow.via(routeFlow(routingTable))
    def routeUnordered[T](routingTable: RoutingTable[S, T]): Flow[R, T, U] = flow.via(routeUnorderedFlow(routingTable))
  }

}
