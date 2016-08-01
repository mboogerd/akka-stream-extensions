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
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import cats.data.{NonEmptyList, OneAnd}
import cats.std.vector._

import scala.concurrent.ExecutionContext

/**
  *
  */
trait ContiguousGroupByOps extends {

  //  def x: Reducible[({type λ[β] = OneAnd[Vector, β]})#λ] = oneAndReducible[Vector]

  implicit class SourceWithContiguousGroupBy[S, T](source: Source[S, T]) {
    def contiguousGroupBy[K](f: S ⇒ K): Source[(K, Source[S, NotUsed]), T] = source.via(new ContiguousGroupBy(f))

    def contiguousGroupByNel[K](f: S ⇒ K)(implicit materializer: Materializer, ec: ExecutionContext): Source[(K, NonEmptyList[S]), T] = {
      source
        .via(new ContiguousGroupBy(f))
        .mapAsync(1) { case (key, src) ⇒ src
          .map(OneAnd(_, Vector.empty[S]))
          .reduce[OneAnd[Vector, S]] { case (vector, oneElem) ⇒ vector.combine(oneElem) }
          .map(nev ⇒ key → nev.copy(tail = nev.tail.toList))
          .runWith(Sink.head)
        }
    }
  }

  implicit class FlowWithContiguousGroupBy[X, S, T](flow: Flow[X, S, T]) {
    def contiguousGroupBy[K](f: S ⇒ K): Flow[X, (K, Source[S, NotUsed]), T] = flow.via(new ContiguousGroupBy(f))

    def contiguousGroupByNel[K](f: S ⇒ K)(implicit materializer: Materializer, ec: ExecutionContext): Flow[X, (K, NonEmptyList[S]), T] = {
      flow
        .via(new ContiguousGroupBy(f))
        .mapAsync(1) { case (key, src) ⇒ src
          .map(OneAnd(_, Vector.empty[S]))
          .reduce[OneAnd[Vector, S]] { case (vector, oneElem) ⇒ vector.combine(oneElem) }
          .map(nev ⇒ key → nev.copy(tail = nev.tail.toList))
          .runWith(Sink.head)
        }
    }
  }

}
