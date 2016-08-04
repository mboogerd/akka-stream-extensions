package com.github.mboogerd.streams.sorted.join

import akka.NotUsed
import akka.stream.scaladsl.{Flow, Source}
import SomeFunctions._
/**
  *
  *
  *
  */

trait Cardinality[S, T] extends InnerJoin[S, T]
  with OuterJoin[S, T]
  with LeftJoin[S, T]
  with RightJoin[S, T]

object SomeFunctions {
  def first[C, ST]: Monotonic[(C, ST), C] = new Monotonic[(C, ST), C] {
    override def apply(v1: (C, ST)): C = v1._1
  }
  def aAndB[A, B]: (A, B) ⇒ (A, B) = (_, _)

  def second[A, B]: (A, B) ⇒ B = (_, b) ⇒ b
}

trait InnerJoin[S, T] {
  type INNER

  protected def joinInner_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, INNER) ⇒ OUT) => Source[OUT, NotUsed]
  protected def joinInner_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, INNER) ⇒ OUT) => Flow[R, OUT, NotUsed]
  protected def joinInner_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, INNER) ⇒ OUT) => Flow[R, OUT, NotUsed]

  def joinInner[C, U, V](s: Source[(C, S), U], t: Source[(C, T), V]): Source[(C, INNER), NotUsed] = joinInner_(s, t)(first, first, Tuple2.apply[C, INNER])
  def joinInner[C, R, U, V](s: Source[(C, S), U], t: Flow[R, (C, T), V]): Flow[R, (C, INNER), NotUsed] = joinInner_(s, t)(first, first, Tuple2.apply[C, INNER])
  def joinInner[C, R, U, V](s: Flow[R, (C, S), U], t: Source[(C, T), V]): Flow[R, (C, INNER), NotUsed] = joinInner_(s, t)(first, first, Tuple2.apply[C, INNER])

  def joinInner[C, R, U, V](s: Source[S, U], t: Source[T, V])(sToC: Monotonic[S, C], tToC: Monotonic[T, C]): Source[INNER, NotUsed] = joinInner_(s, t)(sToC, tToC, second)
  def joinInner[C, R, U, V](s: Source[S, U], t: Flow[R, T, V])(sToC: Monotonic[S, C], tToC: Monotonic[T, C]): Flow[R, INNER, NotUsed] = joinInner_(s, t)(sToC, tToC, second)
  def joinInner[C, R, U, V](s: Flow[R, S, U], t: Source[T, V])(sToC: Monotonic[S, C], tToC: Monotonic[T, C]): Flow[R, INNER, NotUsed] = joinInner_(s, t)(sToC, tToC, second)
}

trait OuterJoin[S, T] {
  type OUTER


  protected def joinOuter_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, OUTER) ⇒ OUT) => Source[OUT, NotUsed]
  protected def joinOuter_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, OUTER) ⇒ OUT) => Flow[R, OUT, NotUsed]
  protected def joinOuter_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, OUTER) ⇒ OUT) => Flow[R, OUT, NotUsed]

  def joinOuter[C, U, V](s: Source[(C, S), U], t: Source[(C, T), V]): Source[(C, OUTER), NotUsed] = joinOuter_(s, t)(first, first, Tuple2.apply[C, OUTER])
  def joinOuter[C, R, U, V](s: Source[(C, S), U], t: Flow[R, (C, T), V]): Flow[R, (C, OUTER), NotUsed] = joinOuter_(s, t)(first, first, Tuple2.apply[C, OUTER])
  def joinOuter[C, R, U, V](s: Flow[R, (C, S), U], t: Source[(C, T), V]): Flow[R, (C, OUTER), NotUsed] = joinOuter_(s, t)(first, first, Tuple2.apply[C, OUTER])

  def joinOuter[C, R, U, V](s: Source[S, U], t: Source[T, V])(sToC: Monotonic[S, C], tToC: Monotonic[T, C]): Source[OUTER, NotUsed] = joinOuter_(s, t)(sToC, tToC, second)
  def joinOuter[C, R, U, V](s: Source[S, U], t: Flow[R, T, V])(sToC: Monotonic[S, C], tToC: Monotonic[T, C]): Flow[R, OUTER, NotUsed] = joinOuter_(s, t)(sToC, tToC, second)
  def joinOuter[C, R, U, V](s: Flow[R, S, U], t: Source[T, V])(sToC: Monotonic[S, C], tToC: Monotonic[T, C]): Flow[R, OUTER, NotUsed] = joinOuter_(s, t)(sToC, tToC, second)
}

trait LeftJoin[S, T] {
  type LEFT

  protected def joinLeft_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, LEFT) ⇒ OUT) => Source[OUT, NotUsed]
  protected def joinLeft_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, LEFT) ⇒ OUT) => Flow[R, OUT, NotUsed]
  protected def joinLeft_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, LEFT) ⇒ OUT) => Flow[R, OUT, NotUsed]

  def joinLeft[C, U, V](s: Source[(C, S), U], t: Source[(C, T), V]): Source[(C, LEFT), NotUsed] = joinLeft_(s, t)(first, first, Tuple2.apply[C, LEFT])
  def joinLeft[C, R, U, V](s: Source[(C, S), U], t: Flow[R, (C, T), V]): Flow[R, (C, LEFT), NotUsed] = joinLeft_(s, t)(first, first, Tuple2.apply[C, LEFT])
  def joinLeft[C, R, U, V](s: Flow[R, (C, S), U], t: Source[(C, T), V]): Flow[R, (C, LEFT), NotUsed] = joinLeft_(s, t)(first, first, Tuple2.apply[C, LEFT])

  def joinLeft[C, R, U, V](s: Source[S, U], t: Source[T, V])(sToC: Monotonic[S, C], tToC: Monotonic[T, C]): Source[LEFT, NotUsed] = joinLeft_(s, t)(sToC, tToC, second)
  def joinLeft[C, R, U, V](s: Source[S, U], t: Flow[R, T, V])(sToC: Monotonic[S, C], tToC: Monotonic[T, C]): Flow[R, LEFT, NotUsed] = joinLeft_(s, t)(sToC, tToC, second)
  def joinLeft[C, R, U, V](s: Flow[R, S, U], t: Source[T, V])(sToC: Monotonic[S, C], tToC: Monotonic[T, C]): Flow[R, LEFT, NotUsed] = joinLeft_(s, t)(sToC, tToC, second)
}

trait RightJoin[S, T] {
  type RIGHT

  protected def joinRight_[A, B, C, U, V, OUT](s: Source[A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, RIGHT) ⇒ OUT) => Source[OUT, NotUsed]
  protected def joinRight_[A, B, C, R, U, V, OUT](s: Source[A, U], t: Flow[R, B, V]): (Monotonic[A, C], Monotonic[B, C], (C, RIGHT) ⇒ OUT) => Flow[R, OUT, NotUsed]
  protected def joinRight_[A, B, C, R, U, V, OUT](s: Flow[R, A, U], t: Source[B, V]): (Monotonic[A, C], Monotonic[B, C], (C, RIGHT) ⇒ OUT) => Flow[R, OUT, NotUsed]

  def joinRight[C, U, V](s: Source[(C, S), U], t: Source[(C, T), V]): Source[(C, RIGHT), NotUsed] = joinRight_(s, t)(first, first, Tuple2.apply[C, RIGHT])
  def joinRight[C, R, U, V](s: Source[(C, S), U], t: Flow[R, (C, T), V]): Flow[R, (C, RIGHT), NotUsed] = joinRight_(s, t)(first, first, Tuple2.apply[C, RIGHT])
  def joinRight[C, R, U, V](s: Flow[R, (C, S), U], t: Source[(C, T), V]): Flow[R, (C, RIGHT), NotUsed] = joinRight_(s, t)(first, first, Tuple2.apply[C, RIGHT])

  def joinRight[C, R, U, V](s: Source[S, U], t: Source[T, V])(sToC: Monotonic[S, C], tToC: Monotonic[T, C]): Source[RIGHT, NotUsed] = joinRight_(s, t)(sToC, tToC, second)
  def joinRight[C, R, U, V](s: Source[S, U], t: Flow[R, T, V])(sToC: Monotonic[S, C], tToC: Monotonic[T, C]): Flow[R, RIGHT, NotUsed] = joinRight_(s, t)(sToC, tToC, second)
  def joinRight[C, R, U, V](s: Flow[R, S, U], t: Source[T, V])(sToC: Monotonic[S, C], tToC: Monotonic[T, C]): Flow[R, RIGHT, NotUsed] = joinRight_(s, t)(sToC, tToC, second)
}