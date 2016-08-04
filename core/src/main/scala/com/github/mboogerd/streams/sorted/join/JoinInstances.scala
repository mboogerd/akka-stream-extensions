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

    override def joinInner[U, V](s: Source[S, U], t: Source[T, V]): Source[(S, T), NotUsed] = leftAndRight(s, t)
    override def joinInner[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (S, T), NotUsed] = leftAndRight(s, t)
    override def joinInner[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (S, T), NotUsed] = leftAndRight(s, t)
  }

  trait OneToSomeInnerJoin[S, T] extends InnerJoin[S, T] {
    override type INNER = (S, NEL[T])

    override def joinInner[U, V](s: Source[S, U], t: Source[T, V]): Source[(S, NEL[T]), NotUsed] = leftSomeRight(s, t)
    override def joinInner[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (S, NEL[T]), NotUsed] = leftSomeRight(s, t)
    override def joinInner[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (S, NEL[T]), NotUsed] = leftSomeRight(s, t)
  }

  trait SomeToOneInnerJoin[S, T] extends InnerJoin[S, T] {
    override type INNER = (NEL[S], T)

    override def joinInner[U, V](s: Source[S, U], t: Source[T, V]): Source[(NEL[S], T), NotUsed] = someLeftAndRight(s, t)
    override def joinInner[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (NEL[S], T), NotUsed] = someLeftAndRight(s, t)
    override def joinInner[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (NEL[S], T), NotUsed] = someLeftAndRight(s, t)
  }

  trait SomeToSomeInnerJoin[S, T] extends InnerJoin[S, T] {
    override type INNER = (NEL[S], NEL[T])

    override def joinInner[U, V](s: Source[S, U], t: Source[T, V]): Source[(NEL[S], NEL[T]), NotUsed] = someLeftAndSomeRight(s, t)
    override def joinInner[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (NEL[S], NEL[T]), NotUsed] = someLeftAndSomeRight(s, t)
    override def joinInner[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (NEL[S], NEL[T]), NotUsed] = someLeftAndSomeRight(s, t)
  }

  /* === OUTER JOIN === */

  trait OneIorOneOuterJoin[S, T] extends OuterJoin[S, T] {
    override type OUTER = S Ior T

    override def joinOuter[U, V](s: Source[S, U], t: Source[T, V]): Source[Ior[S, T], NotUsed] = leftIorRight(s, t)
    override def joinOuter[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, Ior[S, T], NotUsed] = leftIorRight(s, t)
    override def joinOuter[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, Ior[S, T], NotUsed] = leftIorRight(s, t)
  }

  trait MaybeToOneOuterJoin[S, T] extends OuterJoin[S, T] {
    override type OUTER = (Option[S], T)

    override def joinOuter[U, V](s: Source[S, U], t: Source[T, V]): Source[(Option[S], T), NotUsed] = maybeLeftAndRight(s, t)
    override def joinOuter[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (Option[S], T), NotUsed] = maybeLeftAndRight(s, t)
    override def joinOuter[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (Option[S], T), NotUsed] = maybeLeftAndRight(s, t)
  }

  trait OneIorSomeOuterJoin[S, T] extends OuterJoin[S, T] {
    override type OUTER = S Ior NEL[T]

    override def joinOuter[U, V](s: Source[S, U], t: Source[T, V]): Source[Ior[S, NEL[T]], NotUsed] = leftIorSomeRight(s, t)
    override def joinOuter[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, Ior[S, NEL[T]], NotUsed] = leftIorSomeRight(s, t)
    override def joinOuter[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, Ior[S, NEL[T]], NotUsed] = leftIorSomeRight(s, t)
  }

  trait MaybeToSomeOuterJoin[S, T] extends OuterJoin[S, T] {
    override type OUTER = (Option[S], T)

    override def joinOuter[U, V](s: Source[S, U], t: Source[T, V]): Source[(Option[S], T), NotUsed] = maybeLeftAndRight(s, t)
    override def joinOuter[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (Option[S], T), NotUsed] = maybeLeftAndRight(s, t)
    override def joinOuter[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (Option[S], T), NotUsed] = maybeLeftAndRight(s, t)
  }

  trait OneToMaybeOuterJoin[S, T] extends OuterJoin[S, T] {
    override type OUTER = (S, Option[T])

    override def joinOuter[U, V](s: Source[S, U], t: Source[T, V]): Source[(S, Option[T]), NotUsed] = leftAndMaybeRight(s, t)
    override def joinOuter[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (S, Option[T]), NotUsed] = leftAndMaybeRight(s, t)
    override def joinOuter[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (S, Option[T]), NotUsed] = leftAndMaybeRight(s, t)
  }

  trait OneToOneOuterJoin[S, T] extends OuterJoin[S, T] {
    override type OUTER = (S, T)

    override def joinOuter[U, V](s: Source[S, U], t: Source[T, V]): Source[(S, T), NotUsed] = leftAndRight(s, t)
    override def joinOuter[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (S, T), NotUsed] = leftAndRight(s, t)
    override def joinOuter[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (S, T), NotUsed] = leftAndRight(s, t)
  }

  trait OneToSomeOuterJoin[S, T] extends OuterJoin[S, T] {
    override type OUTER = (S, NEL[T])

    override def joinOuter[U, V](s: Source[S, U], t: Source[T, V]): Source[(S, NEL[T]), NotUsed] = leftSomeRight(s, t)
    override def joinOuter[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (S, NEL[T]), NotUsed] = leftSomeRight(s, t)
    override def joinOuter[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (S, NEL[T]), NotUsed] = leftSomeRight(s, t)
  }

  trait SomeIorOneOuterJoin[S, T] extends OuterJoin[S, T] {
    override type OUTER = S Ior T

    override def joinOuter[U, V](s: Source[S, U], t: Source[T, V]): Source[Ior[S, T], NotUsed] = leftIorRight(s, t)
    override def joinOuter[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, Ior[S, T], NotUsed] = leftIorRight(s, t)
    override def joinOuter[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, Ior[S, T], NotUsed] = leftIorRight(s, t)
  }

  trait AnyToOneOuterJoin[S, T] extends OuterJoin[S, T] {
    override type OUTER = (List[S], T)

    override def joinOuter[U, V](s: Source[S, U], t: Source[T, V]): Source[(List[S], T), NotUsed] = anyLeftAndRight(s, t)
    override def joinOuter[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (List[S], T), NotUsed] = anyLeftAndRight(s, t)
    override def joinOuter[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (List[S], T), NotUsed] = anyLeftAndRight(s, t)
  }

  trait SomeIorSomeOuterJoin[S, T] extends OuterJoin[S, T] {
    override type OUTER = NEL[S] Ior NEL[T]

    override def joinOuter[U, V](s: Source[S, U], t: Source[T, V]): Source[Ior[NEL[S], NEL[T]], NotUsed] = someLeftIorSomeRight(s, t)
    override def joinOuter[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, Ior[NEL[S], NEL[T]], NotUsed] = someLeftIorSomeRight(s, t)
    override def joinOuter[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, Ior[NEL[S], NEL[T]], NotUsed] = someLeftIorSomeRight(s, t)
  }

  trait AnyToSomeOuterJoin[S, T] extends OuterJoin[S, T] {
    override type OUTER = (List[S], NEL[T])

    override def joinOuter[U, V](s: Source[S, U], t: Source[T, V]): Source[(List[S], NEL[T]), NotUsed] = anyLeftSomeRight(s, t)
    override def joinOuter[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (List[S], NEL[T]), NotUsed] = anyLeftSomeRight(s, t)
    override def joinOuter[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (List[S], NEL[T]), NotUsed] = anyLeftSomeRight(s, t)
  }

  trait SomeToOneOuterJoin[S, T] extends OuterJoin[S, T] {
    override type OUTER = (NEL[S], T)

    override def joinOuter[U, V](s: Source[S, U], t: Source[T, V]): Source[(NEL[S], T), NotUsed] = someLeftAndRight(s, t)
    override def joinOuter[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (NEL[S], T), NotUsed] = someLeftAndRight(s, t)
    override def joinOuter[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (NEL[S], T), NotUsed] = someLeftAndRight(s, t)
  }

  trait SomeToSomeOuterJoin[S, T] extends OuterJoin[S, T] {
    override type OUTER = (NEL[S], NEL[T])

    override def joinOuter[U, V](s: Source[S, U], t: Source[T, V]): Source[(NEL[S], NEL[T]), NotUsed] = someLeftAndSomeRight(s, t)
    override def joinOuter[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (NEL[S], NEL[T]), NotUsed] = someLeftAndSomeRight(s, t)
    override def joinOuter[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (NEL[S], NEL[T]), NotUsed] = someLeftAndSomeRight(s, t)
  }

  /* === LEFT JOIN === */

  trait OneToMaybeLeftJoin[S, T] extends LeftJoin[S, T] {
    override type LEFT = (S, Option[T])

    override def joinLeft[U, V](s: Source[S, U], t: Source[T, V]): Source[(S, Option[T]), NotUsed] = leftAndMaybeRight(s ,t)
    override def joinLeft[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (S, Option[T]), NotUsed] = leftAndMaybeRight(s ,t)
    override def joinLeft[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (S, Option[T]), NotUsed] = leftAndMaybeRight(s ,t)
  }

  trait OneToOneLeftJoin[S, T] extends LeftJoin[S, T] {
    override type LEFT = (S, T)

    override def joinLeft[U, V](s: Source[S, U], t: Source[T, V]): Source[(S, T), NotUsed] = leftAndRight(s, t)
    override def joinLeft[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (S, T), NotUsed] = leftAndRight(s, t)
    override def joinLeft[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (S, T), NotUsed] = leftAndRight(s, t)
  }

  trait OneToAnyLeftJoin[S, T] extends LeftJoin[S, T] {
    override type LEFT = (S, List[T])

    override def joinLeft[U, V](s: Source[S, U], t: Source[T, V]): Source[(S, List[T]), NotUsed] = leftAnyRight(s, t)
    override def joinLeft[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (S, List[T]), NotUsed] = leftAnyRight(s, t)
    override def joinLeft[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (S, List[T]), NotUsed] = leftAnyRight(s, t)
  }

  trait OneToSomeLeftJoin[S, T] extends LeftJoin[S, T] {
    override type LEFT = (S, NEL[T])

    override def joinLeft[U, V](s: Source[S, U], t: Source[T, V]): Source[(S, NEL[T]), NotUsed] = leftSomeRight(s, t)
    override def joinLeft[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (S, NEL[T]), NotUsed] = leftSomeRight(s, t)
    override def joinLeft[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (S, NEL[T]), NotUsed] = leftSomeRight(s, t)
  }

  trait SomeToMaybeLeftJoin[S, T] extends LeftJoin[S, T] {
    override type LEFT = (NEL[S], Option[T])

    override def joinLeft[U, V](s: Source[S, U], t: Source[T, V]): Source[(NEL[S], Option[T]), NotUsed] = someLeftMaybeRight(s, t)
    override def joinLeft[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (NEL[S], Option[T]), NotUsed] = someLeftMaybeRight(s, t)
    override def joinLeft[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (NEL[S], Option[T]), NotUsed] = someLeftMaybeRight(s, t)
  }

  trait SomeToOneLeftJoin[S, T] extends LeftJoin[S, T] {
    override type LEFT = (NEL[S], T)

    override def joinLeft[U, V](s: Source[S, U], t: Source[T, V]): Source[(NEL[S], T), NotUsed] = someLeftAndRight(s, t)
    override def joinLeft[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (NEL[S], T), NotUsed] = someLeftAndRight(s, t)
    override def joinLeft[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (NEL[S], T), NotUsed] = someLeftAndRight(s, t)
  }

  trait SomeToAnyLeftJoin[S, T] extends LeftJoin[S, T] {
    override type LEFT = (NEL[S], List[T])

    override def joinLeft[U, V](s: Source[S, U], t: Source[T, V]): Source[(NEL[S], List[T]), NotUsed] = someLeftAnyRight(s, t)
    override def joinLeft[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (NEL[S], List[T]), NotUsed] = someLeftAnyRight(s, t)
    override def joinLeft[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (NEL[S], List[T]), NotUsed] = someLeftAnyRight(s, t)
  }

  trait SomeToSomeLeftJoin[S, T] extends LeftJoin[S, T] {
    override type LEFT = (NEL[S], NEL[T])

    override def joinLeft[U, V](s: Source[S, U], t: Source[T, V]): Source[(NEL[S], NEL[T]), NotUsed] = someLeftAndSomeRight(s, t)
    override def joinLeft[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (NEL[S], NEL[T]), NotUsed] = someLeftAndSomeRight(s, t)
    override def joinLeft[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (NEL[S], NEL[T]), NotUsed] = someLeftAndSomeRight(s, t)
  }


  /* === RIGHT JOIN === */

  trait MaybeToOneRightJoin[S, T] extends RightJoin[S, T] {
    override type RIGHT = (Option[S], T)

    override def joinRight[U, V](s: Source[S, U], t: Source[T, V]): Source[(Option[S], T), NotUsed] = ???
    override def joinRight[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (Option[S], T), NotUsed] = ???
    override def joinRight[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (Option[S], T), NotUsed] = ???
  }

  trait MaybeToSomeRightJoin[S, T] extends RightJoin[S, T] {
    override type RIGHT = (Option[S], NEL[T])

    override def joinRight[U, V](s: Source[S, U], t: Source[T, V]): Source[(Option[S], NEL[T]), NotUsed] = maybeLeftSomeRight(s, t)
    override def joinRight[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (Option[S], NEL[T]), NotUsed] = maybeLeftSomeRight(s, t)
    override def joinRight[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (Option[S], NEL[T]), NotUsed] = maybeLeftSomeRight(s, t)
  }

  trait OneToOneRightJoin[S, T] extends RightJoin[S, T] {
    override type RIGHT = (S, T)

    override def joinRight[U, V](s: Source[S, U], t: Source[T, V]): Source[(S, T), NotUsed] = leftAndRight(s, t)
    override def joinRight[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (S, T), NotUsed] = leftAndRight(s, t)
    override def joinRight[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (S, T), NotUsed] = leftAndRight(s, t)
  }

  trait OneToSomeRightJoin[S, T] extends RightJoin[S, T] {
    override type RIGHT = (S, NEL[T])

    override def joinRight[U, V](s: Source[S, U], t: Source[T, V]): Source[(S, NEL[T]), NotUsed] = leftSomeRight(s, t)
    override def joinRight[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (S, NEL[T]), NotUsed] = leftSomeRight(s, t)
    override def joinRight[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (S, NEL[T]), NotUsed] = leftSomeRight(s, t)
  }

  trait AnyToOneRightJoin[S, T] extends RightJoin[S, T] {
    override type RIGHT = (List[S], T)

    override def joinRight[U, V](s: Source[S, U], t: Source[T, V]): Source[(List[S], T), NotUsed] = anyLeftAndRight(s, t)
    override def joinRight[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (List[S], T), NotUsed] = anyLeftAndRight(s, t)
    override def joinRight[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (List[S], T), NotUsed] = anyLeftAndRight(s, t)
  }

  trait AnyToSomeRightJoin[S, T] extends RightJoin[S, T] {
    override type RIGHT = (List[S], NEL[T])

    override def joinRight[U, V](s: Source[S, U], t: Source[T, V]): Source[(List[S], NEL[T]), NotUsed] = anyLeftSomeRight(s, t)
    override def joinRight[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (List[S], NEL[T]), NotUsed] = anyLeftSomeRight(s, t)
    override def joinRight[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (List[S], NEL[T]), NotUsed] = anyLeftSomeRight(s, t)
  }

  trait SomeToOneRightJoin[S, T] extends RightJoin[S, T] {
    override type RIGHT = (NEL[S], T)

    override def joinRight[U, V](s: Source[S, U], t: Source[T, V]): Source[(NEL[S], T), NotUsed] = someLeftAndRight(s, t)
    override def joinRight[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (NEL[S], T), NotUsed] = someLeftAndRight(s, t)
    override def joinRight[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (NEL[S], T), NotUsed] = someLeftAndRight(s, t)
  }

  trait SomeToSomeRightJoin[S, T] extends RightJoin[S, T] {
    override type RIGHT = (NEL[S], NEL[T])

    override def joinRight[U, V](s: Source[S, U], t: Source[T, V]): Source[(NEL[S], NEL[T]), NotUsed] = someLeftAndSomeRight(s, t)
    override def joinRight[R, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (NEL[S], NEL[T]), NotUsed] = someLeftAndSomeRight(s, t)
    override def joinRight[R, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (NEL[S], NEL[T]), NotUsed] = someLeftAndSomeRight(s, t)
  }
}

object JoinInstances extends JoinInstances