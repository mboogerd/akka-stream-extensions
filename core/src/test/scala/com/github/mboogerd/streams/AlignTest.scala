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

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.testkit.TestKit
import cats.Order._
import cats.data.Ior
import cats.data.Ior._
import cats.kernel.std.IntInstances
import cats.kernel.std.map._
import cats.syntax.group._
import org.scalacheck.Gen
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FlatSpecLike, Matchers}
import com.github.mboogerd.streams.sorted.SortedStreamUtil._

import scala.collection.immutable.Seq
import scala.concurrent.Future

class AlignTest extends TestKit(ActorSystem("AlignTest")) with AlignTestSpec with FlatSpecLike with Matchers
  with Eventually with ScalaFutures with GeneratorDrivenPropertyChecks with IntInstances {

  import system.dispatcher

  implicit val actorRefFactory = system

  implicit val materializer = ActorMaterializer()


  behavior of "AlignStreams"

  it should "alignKeys ordered streams of Ints, given an int ordering function" in {
    forAll(genIntStream, genIntStream) { (s1, s2) ⇒

      val testFlow: Future[Seq[Ior[Int, Int]]] = Source(s1)
        .align(Source(s2))
        .runWith(Sink.seq)

      val testResult = testFlow.map(_.map(flattenSymmetricIor))

      val expectedElements = (s1 ++ s2).toSet.toSeq.sorted
      testResult.futureValue should contain theSameElementsInOrderAs expectedElements
    }
  }

  it should "align streams of key-value pairs, assuming that they are united.sorted on key" in {
    forAll(genIntPairStream, genIntPairStream) { (s1, s2) ⇒

      // align streams by key
      val testFlow: Future[Seq[(Int, Ior[Int, Int])]] = Source(s1)
        .alignKeys(Source(s2))
        .runWith(Sink.seq)

      // prepare outcome for comparison
      val testResult = testFlow
        .map(_.toMap)
        .map(_.mapValues(sumIor))

      val expectedOutcome: Map[Int, Int] = s1.toMap |+| s2.toMap

      testResult.futureValue should contain theSameElementsAs expectedOutcome
    }
  }

}


trait AlignTestSpec {

  /**
    * Generates an arbitrary stream of integers, from some initial number, incrementing with arbitrary step
    * capped to an arbitrary size. This particular generator is such that it generates streams that are likely, but not
    * guaranteed to have overlap with one another
    */
  val genIntStream: Gen[Stream[Int]] = for {
    init <- Gen.choose(0, 20)
    step <- Gen.choose(1, 10)
    size <- Gen.choose(0, 25)
  } yield Stream.from(init, step).take(size)

  val genIntPairStream: Gen[Stream[(Int, Int)]] = for {
    s1 ← genIntStream
    s2 ← genIntStream
  } yield s1.zip(s2)

  /**
    * Returns the one element included in the inclusive-or instance, or throws a match-exception if the instance
    * contains two elements that are not equivalent
    */
  def flattenSymmetricIor[S](ior: S Ior S): S = ior match {
    case Left(l) ⇒ l
    case Right(r) ⇒ r
    case Both(l, r) if l == r ⇒ l
  }

  /**
    * Returns the one element included in the inclusive-or instance, or throws a match-exception if the instance
    * contains two elements that are not equivalent
    */
  def sumIor(ior: Int Ior Int): Int = ior match {
    case Left(l) ⇒ l
    case Right(r) ⇒ r
    case Both(l, r) ⇒ l + r
  }
}
