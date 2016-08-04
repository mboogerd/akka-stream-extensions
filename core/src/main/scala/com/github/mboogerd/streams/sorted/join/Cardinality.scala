package com.github.mboogerd.streams.sorted.join

import akka.NotUsed
import akka.stream.scaladsl.{Flow, Source}

/**
  *
  */
trait Cardinality[S, T] extends InnerJoin[S, T]
  with OuterJoin[S, T]
  with LeftJoin[S, T]
  with RightJoin[S, T]

trait InnerJoin[S, T] {
  type INNER

  def joinInner[U, V](s: Source[S, U], t: Source[T, V]): Source[INNER, NotUsed]
  def joinInner[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, INNER, NotUsed]
  def joinInner[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, INNER, NotUsed]
}

trait OuterJoin[S, T] {
  type OUTER

  def joinOuter[U, V](s: Source[S, U], t: Source[T, V]): Source[OUTER, NotUsed]
  def joinOuter[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, OUTER, NotUsed]
  def joinOuter[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, OUTER, NotUsed]
}

trait LeftJoin[S, T] {
  type LEFT

  def joinLeft[U, V](s: Source[S, U], t: Source[T, V]): Source[LEFT, NotUsed]
  def joinLeft[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, LEFT, NotUsed]
  def joinLeft[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, LEFT, NotUsed]
}

trait RightJoin[S, T] {
  type RIGHT

  def joinRight[U, V](s: Source[S, U], t: Source[T, V]): Source[RIGHT, NotUsed]
  def joinRight[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, RIGHT, NotUsed]
  def joinRight[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, RIGHT, NotUsed]
}