package com.github.mboogerd.streams.pubsub

import akka.stream.scaladsl.Sink
import com.github.mboogerd.streams.pubsub.PubSubActor.Subscribe
import org.reactivestreams.{Publisher, Subscriber}

/**
  *
  */
object PubSub {
  def pubSubSink[T]: Sink[T, PubSub[T]] = {
    Sink.actorSubscriber[T](PubSubActor.props[T]).mapMaterializedValue(ref ⇒ new PubSub[T]{
      def publisher: Publisher[T] = new Publisher[T] {
        override def subscribe(s: Subscriber[_ >: T]): Unit = ref ! Subscribe(s)
      }
    })
  }
}

trait PubSub[T] {
  def publisher: Publisher[T]
}

