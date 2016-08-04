package com.github.mboogerd.streams.sorted.join

import akka.NotUsed
import akka.stream.scaladsl.{Flow, Source}
import cats.data.Ior

/**
  *
  */
trait JoinInstances extends JoinDefinitionHelper {


  /* === INNER JOIN === */

  trait OneToOneInnerJoin[S, T] extends InnerJoin[S, T] {
    override type INNER = (S, T)

    override protected def joinInner_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, B)) ⇒ OUT) ⇒ Source[OUT, NotUsed] = leftAndRight(s, t)
    override protected def joinInner_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, B)) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = leftAndRight(s, t)
    override protected def joinInner_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, B)) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = leftAndRight(s, t)
  }

  trait OneToSomeInnerJoin[S, T] extends InnerJoin[S, T] {
    override type INNER = (S, NEL[T])

    override protected def joinInner_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, NEL[B])) ⇒ OUT) ⇒ Source[OUT, NotUsed] = leftSomeRight(s, t)
    override protected def joinInner_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, NEL[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = leftSomeRight(s, t)
    override protected def joinInner_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, NEL[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = leftSomeRight(s, t)
  }

  trait SomeToOneInnerJoin[S, T] extends InnerJoin[S, T] {
    override type INNER = (NEL[S], T)

    override protected def joinInner_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], B)) ⇒ OUT) ⇒ Source[OUT, NotUsed] = someLeftAndRight(s, t)
    override protected def joinInner_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], B)) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = someLeftAndRight(s, t)
    override protected def joinInner_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], B)) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = someLeftAndRight(s, t)
  }

  trait SomeToSomeInnerJoin[S, T] extends InnerJoin[S, T] {
    override type INNER = (NEL[S], NEL[T])

    override protected def joinInner_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], NEL[B])) ⇒ OUT) ⇒ Source[OUT, NotUsed] = someLeftAndSomeRight(s, t)
    override protected def joinInner_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], NEL[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = someLeftAndSomeRight(s, t)
    override protected def joinInner_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], NEL[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = someLeftAndSomeRight(s, t)
  }

  /* === OUTER JOIN === */

  trait OneIorOneOuterJoin[S, T] extends OuterJoin[S, T] {
    override type OUTER = S Ior T

    override protected def joinOuter_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, Ior[A, B]) ⇒ OUT) ⇒ Source[OUT, NotUsed] = leftIorRight(s, t)
    override protected def joinOuter_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, Ior[A, B]) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = leftIorRight(s, t)
    override protected def joinOuter_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, Ior[A, B]) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = leftIorRight(s, t)
  }

  trait MaybeToOneOuterJoin[S, T] extends OuterJoin[S, T] {
    override type OUTER = (Option[S], T)

    override protected def joinOuter_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (Option[A], B)) ⇒ OUT) ⇒ Source[OUT, NotUsed] = maybeLeftAndRight(s, t)
    override protected def joinOuter_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (Option[A], B)) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = maybeLeftAndRight(s, t)
    override protected def joinOuter_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (Option[A], B)) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = maybeLeftAndRight(s, t)
  }

  trait OneIorSomeOuterJoin[S, T] extends OuterJoin[S, T] {
    override type OUTER = S Ior NEL[T]

    override protected def joinOuter_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, Ior[A, NEL[B]]) ⇒ OUT) ⇒ Source[OUT, NotUsed] = leftIorSomeRight(s, t)
    override protected def joinOuter_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, Ior[A, NEL[B]]) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = leftIorSomeRight(s, t)
    override protected def joinOuter_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, Ior[A, NEL[B]]) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = leftIorSomeRight(s, t)
  }

  trait MaybeToSomeOuterJoin[S, T] extends OuterJoin[S, T] {
    override type OUTER = (Option[S], T)

    override protected def joinOuter_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (Option[A], B)) ⇒ OUT) ⇒ Source[OUT, NotUsed] = maybeLeftAndRight(s, t)
    override protected def joinOuter_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (Option[A], B)) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = maybeLeftAndRight(s, t)
    override protected def joinOuter_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (Option[A], B)) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = maybeLeftAndRight(s, t)
  }

  trait OneToMaybeOuterJoin[S, T] extends OuterJoin[S, T] {
    override type OUTER = (S, Option[T])

    override protected def joinOuter_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, Option[B])) ⇒ OUT) ⇒ Source[OUT, NotUsed] = leftAndMaybeRight(s, t)
    override protected def joinOuter_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, Option[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = leftAndMaybeRight(s, t)
    override protected def joinOuter_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, Option[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = leftAndMaybeRight(s, t)
  }

  trait OneToOneOuterJoin[S, T] extends OuterJoin[S, T] {
    override type OUTER = (S, T)

    override protected def joinOuter_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, B)) ⇒ OUT) ⇒ Source[OUT, NotUsed] = leftAndRight(s, t)
    override protected def joinOuter_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, B)) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = leftAndRight(s, t)
    override protected def joinOuter_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, B)) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = leftAndRight(s, t)
  }

  trait OneToSomeOuterJoin[S, T] extends OuterJoin[S, T] {
    override type OUTER = (S, NEL[T])

    override protected def joinOuter_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, NEL[B])) ⇒ OUT) ⇒ Source[OUT, NotUsed] = leftSomeRight(s, t)
    override protected def joinOuter_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, NEL[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = leftSomeRight(s, t)
    override protected def joinOuter_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, NEL[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = leftSomeRight(s, t)
  }

  trait SomeIorOneOuterJoin[S, T] extends OuterJoin[S, T] {
    override type OUTER = S Ior T

    override protected def joinOuter_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, Ior[A, B]) ⇒ OUT) ⇒ Source[OUT, NotUsed] = leftIorRight(s, t)
    override protected def joinOuter_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, Ior[A, B]) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = leftIorRight(s, t)
    override protected def joinOuter_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, Ior[A, B]) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = leftIorRight(s, t)
  }

  trait AnyToOneOuterJoin[S, T] extends OuterJoin[S, T] {
    override type OUTER = (List[S], T)

    override protected def joinOuter_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (List[A], B)) ⇒ OUT) ⇒ Source[OUT, NotUsed] = anyLeftAndRight(s, t)
    override protected def joinOuter_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (List[A], B)) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = anyLeftAndRight(s, t)
    override protected def joinOuter_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (List[A], B)) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = anyLeftAndRight(s, t)
  }

  trait SomeIorSomeOuterJoin[S, T] extends OuterJoin[S, T] {
    override type OUTER = NEL[S] Ior NEL[T]

    override protected def joinOuter_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, Ior[NEL[A], NEL[B]]) ⇒ OUT) ⇒ Source[OUT, NotUsed] = someLeftIorSomeRight(s, t)
    override protected def joinOuter_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, Ior[NEL[A], NEL[B]]) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = someLeftIorSomeRight(s, t)
    override protected def joinOuter_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, Ior[NEL[A], NEL[B]]) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = someLeftIorSomeRight(s, t)
  }

  trait AnyToSomeOuterJoin[S, T] extends OuterJoin[S, T] {
    override type OUTER = (List[S], NEL[T])

    override protected def joinOuter_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (List[A], NEL[B])) ⇒ OUT) ⇒ Source[OUT, NotUsed] = anyLeftSomeRight(s, t)
    override protected def joinOuter_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (List[A], NEL[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = anyLeftSomeRight(s, t)
    override protected def joinOuter_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (List[A], NEL[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = anyLeftSomeRight(s, t)
  }

  trait SomeToOneOuterJoin[S, T] extends OuterJoin[S, T] {
    override type OUTER = (NEL[S], T)

    override protected def joinOuter_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], B)) ⇒ OUT) ⇒ Source[OUT, NotUsed] = someLeftAndRight(s, t)
    override protected def joinOuter_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], B)) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = someLeftAndRight(s, t)
    override protected def joinOuter_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], B)) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = someLeftAndRight(s, t)
  }

  trait SomeToSomeOuterJoin[S, T] extends OuterJoin[S, T] {
    override type OUTER = (NEL[S], NEL[T])

    override protected def joinOuter_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], NEL[B])) ⇒ OUT) ⇒ Source[OUT, NotUsed] = someLeftAndSomeRight(s, t)
    override protected def joinOuter_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], NEL[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = someLeftAndSomeRight(s, t)
    override protected def joinOuter_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], NEL[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = someLeftAndSomeRight(s, t)
  }

  /* === LEFT JOIN === */

  trait OneToMaybeLeftJoin[S, T] extends LeftJoin[S, T] {
    override type LEFT = (S, Option[T])

    override protected def joinLeft_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, Option[B])) ⇒ OUT) ⇒ Source[OUT, NotUsed] = leftAndMaybeRight(s ,t)
    override protected def joinLeft_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, Option[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = leftAndMaybeRight(s ,t)
    override protected def joinLeft_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, Option[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = leftAndMaybeRight(s ,t)
  }

  trait OneToOneLeftJoin[S, T] extends LeftJoin[S, T] {
    override type LEFT = (S, T)

    override protected def joinLeft_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, B)) ⇒ OUT) ⇒ Source[OUT, NotUsed] = leftAndRight(s, t)
    override protected def joinLeft_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, B)) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = leftAndRight(s, t)
    override protected def joinLeft_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, B)) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = leftAndRight(s, t)
  }

  trait OneToAnyLeftJoin[S, T] extends LeftJoin[S, T] {
    override type LEFT = (S, List[T])

    override protected def joinLeft_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, List[B])) ⇒ OUT) ⇒ Source[OUT, NotUsed] = leftAnyRight(s, t)
    override protected def joinLeft_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, List[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = leftAnyRight(s, t)
    override protected def joinLeft_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, List[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = leftAnyRight(s, t)
  }

  trait OneToSomeLeftJoin[S, T] extends LeftJoin[S, T] {
    override type LEFT = (S, NEL[T])

    override protected def joinLeft_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, NEL[B])) ⇒ OUT) ⇒ Source[OUT, NotUsed] = leftSomeRight(s, t)
    override protected def joinLeft_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, NEL[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = leftSomeRight(s, t)
    override protected def joinLeft_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, NEL[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = leftSomeRight(s, t)
  }

  trait SomeToMaybeLeftJoin[S, T] extends LeftJoin[S, T] {
    override type LEFT = (NEL[S], Option[T])

    override protected def joinLeft_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], Option[B])) ⇒ OUT) ⇒ Source[OUT, NotUsed] = someLeftMaybeRight(s, t)
    override protected def joinLeft_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], Option[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = someLeftMaybeRight(s, t)
    override protected def joinLeft_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], Option[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = someLeftMaybeRight(s, t)
  }

  trait SomeToOneLeftJoin[S, T] extends LeftJoin[S, T] {
    override type LEFT = (NEL[S], T)


    override protected def joinLeft_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], B)) ⇒ OUT) ⇒ Source[OUT, NotUsed] = someLeftAndRight(s, t)
    override protected def joinLeft_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], B)) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = someLeftAndRight(s, t)
    override protected def joinLeft_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], B)) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = someLeftAndRight(s, t)
  }

  trait SomeToAnyLeftJoin[S, T] extends LeftJoin[S, T] {
    override type LEFT = (NEL[S], List[T])

    override protected def joinLeft_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], List[B])) ⇒ OUT) ⇒ Source[OUT, NotUsed] = someLeftAnyRight(s, t)
    override protected def joinLeft_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], List[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = someLeftAnyRight(s, t)
    override protected def joinLeft_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], List[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = someLeftAnyRight(s, t)
  }

  trait SomeToSomeLeftJoin[S, T] extends LeftJoin[S, T] {
    override type LEFT = (NEL[S], NEL[T])

    override protected def joinLeft_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], NEL[B])) ⇒ OUT) ⇒ Source[OUT, NotUsed] = someLeftAndSomeRight(s, t)
    override protected def joinLeft_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], NEL[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = someLeftAndSomeRight(s, t)
    override protected def joinLeft_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], NEL[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = someLeftAndSomeRight(s, t)
  }


  /* === RIGHT JOIN === */

  trait MaybeToOneRightJoin[S, T] extends RightJoin[S, T] {
    override type RIGHT = (Option[S], T)

    override protected def joinRight_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (Option[A], B)) ⇒ OUT) ⇒ Source[OUT, NotUsed] = maybeLeftAndRight(s, t)
    override protected def joinRight_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (Option[A], B)) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = maybeLeftAndRight(s, t)
    override protected def joinRight_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (Option[A], B)) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = maybeLeftAndRight(s, t)
  }

  trait MaybeToSomeRightJoin[S, T] extends RightJoin[S, T] {
    override type RIGHT = (Option[S], NEL[T])

    override protected def joinRight_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (Option[A], NEL[B])) ⇒ OUT) ⇒ Source[OUT, NotUsed] = maybeLeftSomeRight(s, t)
    override protected def joinRight_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (Option[A], NEL[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = maybeLeftSomeRight(s, t)
    override protected def joinRight_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (Option[A], NEL[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = maybeLeftSomeRight(s, t)
  }

  trait OneToOneRightJoin[S, T] extends RightJoin[S, T] {
    override type RIGHT = (S, T)

    override protected def joinRight_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, B)) ⇒ OUT) ⇒ Source[OUT, NotUsed] = leftAndRight(s, t)
    override protected def joinRight_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, B)) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = leftAndRight(s, t)
    override protected def joinRight_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, B)) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = leftAndRight(s, t)
  }

  trait OneToSomeRightJoin[S, T] extends RightJoin[S, T] {
    override type RIGHT = (S, NEL[T])


    override protected def joinRight_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, NEL[B])) ⇒ OUT) ⇒ Source[OUT, NotUsed] = leftSomeRight(s, t)
    override protected def joinRight_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, NEL[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = leftSomeRight(s, t)
    override protected def joinRight_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (A, NEL[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = leftSomeRight(s, t)
  }

  trait AnyToOneRightJoin[S, T] extends RightJoin[S, T] {
    override type RIGHT = (List[S], T)

    override protected def joinRight_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (List[A], B)) ⇒ OUT) ⇒ Source[OUT, NotUsed] = anyLeftAndRight(s, t)
    override protected def joinRight_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (List[A], B)) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = anyLeftAndRight(s, t)
    override protected def joinRight_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (List[A], B)) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = anyLeftAndRight(s, t)
  }

  trait AnyToSomeRightJoin[S, T] extends RightJoin[S, T] {
    override type RIGHT = (List[S], NEL[T])

    override protected def joinRight_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (List[A], NEL[B])) ⇒ OUT) ⇒ Source[OUT, NotUsed] = anyLeftSomeRight(s, t)
    override protected def joinRight_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (List[A], NEL[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = anyLeftSomeRight(s, t)
    override protected def joinRight_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (List[A], NEL[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = anyLeftSomeRight(s, t)
  }

  trait SomeToOneRightJoin[S, T] extends RightJoin[S, T] {
    override type RIGHT = (NEL[S], T)

    override protected def joinRight_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], B)) ⇒ OUT) ⇒ Source[OUT, NotUsed] = someLeftAndRight(s, t)
    override protected def joinRight_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], B)) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = someLeftAndRight(s, t)
    override protected def joinRight_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], B)) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = someLeftAndRight(s, t)
  }

  trait SomeToSomeRightJoin[S, T] extends RightJoin[S, T] {
    override type RIGHT = (NEL[S], NEL[T])

    override protected def joinRight_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], NEL[B])) ⇒ OUT) ⇒ Source[OUT, NotUsed] = someLeftAndSomeRight(s, t)
    override protected def joinRight_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], NEL[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = someLeftAndSomeRight(s, t)
    override protected def joinRight_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, (NEL[A], NEL[B])) ⇒ OUT) ⇒ Flow[R, OUT, NotUsed] = someLeftAndSomeRight(s, t)
  }
}

object JoinInstances extends JoinInstances