package com.github.mboogerd.streams

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import akka.testkit.TestKitBase
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpecLike, Matchers}

/**
  *
  */
trait TestSpec extends TestKitBase with FlatSpecLike with Matchers with ScalaFutures {
  override implicit lazy val system: ActorSystem = ActorSystem("test")
  implicit lazy val materializer: Materializer = ActorMaterializer()

}
