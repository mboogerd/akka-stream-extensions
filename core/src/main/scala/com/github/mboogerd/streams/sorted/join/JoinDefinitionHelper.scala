package com.github.mboogerd.streams.sorted.join

import akka.NotUsed
import akka.stream.scaladsl.{Flow, Source}
import cats.data.Ior

/**
  *
  */
trait JoinDefinitionHelper{

  /* symmetric instances - although generalizing to OUT has removed the little advantage that was available */

  protected[join] def leftIorRight[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, Ior[A, B]) ⇒ OUT) ⇒ Source[OUT, NotUsed] = ???
  protected[join] def leftIorRight[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, Ior[A, B]) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = ???
  protected[join] def leftIorRight[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, Ior[A, B]) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = ???

  protected[join] def leftAndRight[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, B)) ⇒ OUT) ⇒ Source[OUT, NotUsed] = ???
  protected[join] def leftAndRight[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, B)) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = ???
  protected[join] def leftAndRight[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, B)) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = ???

  protected[join] def someLeftAndSomeRight[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], NEL[B])) ⇒ OUT) ⇒ Source[OUT, NotUsed] = ???
  protected[join] def someLeftAndSomeRight[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], NEL[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = ???
  protected[join] def someLeftAndSomeRight[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], NEL[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = ???

  protected[join] def someLeftIorSomeRight[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, NEL[A] Ior NEL[B]) ⇒ OUT) ⇒ Source[OUT, NotUsed] = ???
  protected[join] def someLeftIorSomeRight[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, NEL[A] Ior NEL[B]) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = ???
  protected[join] def someLeftIorSomeRight[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, NEL[A] Ior NEL[B]) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = ???


  /* asymmetric instances */

  protected[join] def maybeLeftAndRight[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (Option[A], B)) ⇒ OUT) ⇒ Source[OUT, NotUsed] = ???
  protected[join] def maybeLeftAndRight[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (Option[A], B)) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = ???
  protected[join] def maybeLeftAndRight[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (Option[A], B)) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = ???

  protected[join] def leftAndMaybeRight[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, Option[B])) ⇒ OUT) ⇒ Source[OUT, NotUsed] = ???
  protected[join] def leftAndMaybeRight[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, Option[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = ???
  protected[join] def leftAndMaybeRight[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, Option[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = ???


  protected[join] def maybeLeftSomeRight[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (Option[A], NEL[B])) ⇒ OUT) ⇒ Source[OUT, NotUsed] = ???
  protected[join] def maybeLeftSomeRight[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (Option[A], NEL[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = ???
  protected[join] def maybeLeftSomeRight[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (Option[A], NEL[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = ???

  protected[join] def someLeftMaybeRight[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], Option[B])) ⇒ OUT) ⇒ Source[OUT, NotUsed] = ???
  protected[join] def someLeftMaybeRight[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], Option[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = ???
  protected[join] def someLeftMaybeRight[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], Option[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = ???


  protected[join] def leftIorSomeRight[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, A Ior NEL[B]) ⇒ OUT) ⇒ Source[OUT, NotUsed] = ???
  protected[join] def leftIorSomeRight[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, A Ior NEL[B]) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = ???
  protected[join] def leftIorSomeRight[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, A Ior NEL[B]) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = ???

  protected[join] def someLeftIorRight[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, NEL[A] Ior B) ⇒ OUT) ⇒ Source[OUT, NotUsed] = ???
  protected[join] def someLeftIorRight[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, NEL[A] Ior B) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = ???
  protected[join] def someLeftIorRight[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, NEL[A] Ior B) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = ???


  protected[join] def leftAnyRight[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, List[B])) ⇒ OUT) ⇒ Source[OUT, NotUsed] = ???
  protected[join] def leftAnyRight[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, List[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = ???
  protected[join] def leftAnyRight[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, List[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = ???

  protected[join] def anyLeftAndRight[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (List[A], B)) ⇒ OUT) ⇒ Source[OUT, NotUsed] = ???
  protected[join] def anyLeftAndRight[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (List[A], B)) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = ???
  protected[join] def anyLeftAndRight[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (List[A], B)) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = ???


  protected[join] def leftSomeRight[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, NEL[B])) ⇒ OUT) ⇒ Source[OUT, NotUsed] = ???
  protected[join] def leftSomeRight[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, NEL[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = ???
  protected[join] def leftSomeRight[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, NEL[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = ???

  protected[join] def someLeftAndRight[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], B)) ⇒ OUT) ⇒ Source[OUT, NotUsed] = ???
  protected[join] def someLeftAndRight[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], B)) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = ???
  protected[join] def someLeftAndRight[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], B)) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = ???


  protected[join] def anyLeftSomeRight[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (List[A], NEL[B])) ⇒ OUT) ⇒ Source[OUT, NotUsed] = ???
  protected[join] def anyLeftSomeRight[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (List[A], NEL[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = ???
  protected[join] def anyLeftSomeRight[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (List[A], NEL[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = ???

  protected[join] def someLeftAnyRight[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], List[B])) ⇒ OUT) ⇒ Source[OUT, NotUsed] = ???
  protected[join] def someLeftAnyRight[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], List[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = ???
  protected[join] def someLeftAnyRight[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], List[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = ???
}