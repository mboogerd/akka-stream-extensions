package com.github.mboogerd.streams

import akka.NotUsed
import akka.stream.impl.PublisherSource
import akka.stream.impl.Stages.DefaultAttributes
import akka.stream.scaladsl.{Sink, Source}
import com.github.mboogerd.streams.pubsub.PubSub
import org.reactivestreams.Publisher

import scala.collection.immutable.Seq
import scala.concurrent.Future

/**
  *
  */
class PubSubTest extends TestSpec {

  behavior of "PubSub"

  it should "behave as a normal stream with a single subscriber" in withPubSub { pubsub ⇒
    val derivedStream: Source[Int, NotUsed] = sourceFromPublisher(pubsub.publisher)
    val values = derivedStream.take(10).runWith(Sink.seq)

    values.futureValue should contain theSameElementsInOrderAs (1 to 10)
  }


  /* This fails due to sources having internal demand, which seems to be impossible to control from the API */
  ignore should "allow sequential subscriptions" in withPubSub { pubsub ⇒
    val firstSequence: Seq[Int] = sourceFromPublisher(pubsub.publisher).take(10).runWith(Sink.seq).futureValue
    firstSequence should contain theSameElementsInOrderAs (1 to 10)

    val secondSequence: Seq[Int] = sourceFromPublisher(pubsub.publisher).take(10).runWith(Sink.seq).futureValue
    secondSequence should contain theSameElementsInOrderAs (11 to 20)
  }

  /* This fails due to sources having internal demand, which seems to be impossible to control from the API */
  ignore should "allow concurrent subscriptions" in withPubSub { pubsub ⇒
    val futureSeq1: Future[Seq[Int]] = sourceFromPublisher(pubsub.publisher).take(100).runWith(Sink.seq)
    val futureSeq2: Future[Seq[Int]] = sourceFromPublisher(pubsub.publisher).take(100).runWith(Sink.seq)

    val sequence = (futureSeq1.futureValue ++ futureSeq2.futureValue).sorted
    sequence should contain theSameElementsInOrderAs(1 to 200)
  }

  def withIntegerStream(test: Source[Int, NotUsed] ⇒ Any): Unit = {
    val integerStream = Source.fromIterator(() ⇒ Iterator.from(1))
    test(integerStream)
  }

  def withPubSub(test: PubSub[Int] ⇒ Any): Unit = withIntegerStream(source ⇒ test(source.runWith(PubSub.pubSubSink)))

  def sourceFromPublisher[T](publisher: Publisher[T]): Source[T, NotUsed] =
    new Source(new PublisherSource(publisher, DefaultAttributes.inputBufferOne, Source.shape("PublisherSource"))).addAttributes(DefaultAttributes.inputBufferOne)
  //    Source.fromPublisher()
}
