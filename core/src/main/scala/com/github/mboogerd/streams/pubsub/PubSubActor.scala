package com.github.mboogerd.streams.pubsub

import akka.actor.{Actor, PoisonPill, Props}
import akka.stream.actor.ActorSubscriberMessage.{OnComplete, OnError, OnNext}
import akka.stream.actor.{ActorSubscriber, RequestStrategy}
import org.reactivestreams.{Subscriber, Subscription}

import scala.collection.immutable.Queue

/**
  *
  */
object PubSubActor {

  case class Subscribe[X](subscriber: Subscriber[X])

  case class Cancel[X](subscriber: Subscriber[X])

  case class Request[X](subscriber: Subscriber[X], demand: Long)

  case class Demand[X](subscriber: Subscriber[X], demand: Long)

  case class Demands[X](active: Option[Demand[X]] = None, queued: Queue[Demand[X]] = Queue.empty)


  def props[T]: Props = Props(new PubSubActor[T])
}

class PubSubActor[T] extends Actor with ActorSubscriber {

  import PubSubActor._

  var buffered: Queue[Any] = Queue.empty
  var subscribers = Set.empty[Subscriber[Any]]
  var demands: Demands[Any] = Demands()
  var requestCount: Long = 0

  override protected def requestStrategy: RequestStrategy = new RequestStrategy {
    override def requestDemand(remainingRequested: Int): Int = {
      println(s"remainingRequested: $remainingRequested")
      math.min(requestCount - remainingRequested, Int.MaxValue).toInt
    }

  }

  override def receive: Receive = subscribe orElse processSubscriberMessage orElse processPublisherMessage

  /**
    * Handles new subscriptions
    */
  def subscribe(): Receive = {
    case Subscribe(subscriber: Subscriber[Any]) if !subscribers.contains(subscriber) ⇒
      subscribers += subscriber
      val subscription = newSubscription(subscriber)
      subscriber.onSubscribe(subscription)
  }

  /**
    * Handles the messages that subscribers can send
    */
  def processSubscriberMessage(): Receive = {
    case Cancel(subscriber: Subscriber[Any]) ⇒
      demands = removeDemands(demands, subscriber)
      subscribers -= subscriber
    case Request(subscriber: Subscriber[Any], demand) if demand > 0 ⇒
      demands = queueDemand(demands, Demand(subscriber, demand))
      requestCount += demand
      propagateBufferToSubscribers()
  }

  /**
    * Handles the messages that the parent may send.
    */
  def processPublisherMessage(): Receive = {
    case OnNext(elem: Any) ⇒
      requestCount -= 1
      buffered = buffered.enqueue(elem)
      propagateBufferToSubscribers()
    case OnError(t) ⇒ terminateStream(subscribers)(_.onError(t))
    case OnComplete ⇒ terminateStream(subscribers)(_.onComplete())
  }


  def propagateBufferToSubscribers(): Unit = {
    println(requestCount, subscribers, buffered, demands)
    (buffered.dequeueOption, demands) match {
      case (Some((element, queuedElems)), Demands(Some(demand), queudDemands)) ⇒
        // propagate the element
        demand.subscriber.onNext(element)
        // store the new queue
        buffered = queuedElems
        // recalculate demands
        val newActiveDemand = Some(demand.copy(demand = demand.demand - 1))
        demands = ensureActiveDemand(Demands(newActiveDemand, queudDemands))
        // continue trying to empty the buffer
        propagateBufferToSubscribers()
      case _ ⇒
      // do nothing if we do not both have elements and demand
    }
  }

  /**
    * Eliminates all demands from the given subscriber of the queue of demands
    */
  def removeDemands(demands: Demands[Any], subscriber: Subscriber[Any]) = ensureActiveDemand(Demands(
    demands.active.filterNot(_.subscriber == subscriber),
    demands.queued.filterNot(_.subscriber == subscriber)
  ))

  /**
    * Registers new demand to be fulfilled
    */
  def queueDemand(demands: Demands[Any], demand: Demand[Any]) =
  ensureActiveDemand(demands.copy(queued = demands.queued.enqueue(demand)))

  /**
    * Makes sure that active-demand has an element set, if possible
    */
  def ensureActiveDemand: Demands[Any] ⇒ Demands[Any] = {
    case Demands(None, demand +: queued) ⇒ Demands(Some(demand), queued)
    case demands@Demands(Some(Demand(_, 0)), demand +: queued) ⇒ Demands(Some(demand), queued)
    case demands@Demands(Some(Demand(_, 0)), demand) ⇒ Demands(None, demand)
    case demands@Demands(_, _) ⇒ demands
  }

  /**
    * Notifies all subscribers that this stream is terminating, then commits suicide.
    */
  def terminateStream(subscribers: Set[Subscriber[Any]])(terminateSubscriber: Subscriber[Any] ⇒ Unit) = {
    subscribers.foreach(terminateSubscriber)
    self ! PoisonPill
  }

  /**
    * Creates a new subscription that reifies method calls and dispatches them as messages to us.
    */
  def newSubscription(subscriber: Subscriber[Any]) = new Subscription {
    override def cancel(): Unit = self ! Cancel(subscriber)
    override def request(demand: Long): Unit = self ! Request(subscriber, demand)
  }
}