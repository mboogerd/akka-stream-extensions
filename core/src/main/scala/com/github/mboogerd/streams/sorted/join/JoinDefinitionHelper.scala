package com.github.mboogerd.streams.sorted.join

import akka.NotUsed
import akka.stream.scaladsl.{Flow, Source}
import cats.data.Ior

/**
  *
  */
trait JoinDefinitionHelper{

  /* symmetric instances */

  protected[join] def leftIorRight[S, T, U, V](s: Source[S, U], t: Source[T, V]): Source[Ior[S, T], NotUsed] = ???
  protected[join] def leftIorRight[R, S, T, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, Ior[S, T], NotUsed] = ???
  protected[join] def leftIorRight[R, S, T, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, Ior[S, T], NotUsed] = leftIorRight(t, s).map(_.swap)

  protected[join] def leftAndRight[S, T, U, V](s: Source[S, U], t: Source[T, V]): Source[(S, T), NotUsed] = ???
  protected[join] def leftAndRight[R, S, T, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (S, T), NotUsed] = ???
  protected[join] def leftAndRight[R, S, T, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (S, T), NotUsed] = leftAndRight(t, s).map(_.swap)

  protected[join] def someLeftAndSomeRight[S, T, U, V](s: Source[S, U], t: Source[T, V]): Source[(NEL[S], NEL[T]), NotUsed] = ???
  protected[join] def someLeftAndSomeRight[R, S, T, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (NEL[S], NEL[T]), NotUsed] = ???
  protected[join] def someLeftAndSomeRight[R, S, T, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (NEL[S], NEL[T]), NotUsed] = someLeftAndSomeRight(t, s).map(_.swap)

  protected[join] def someLeftIorSomeRight[S, T, U, V](s: Source[S, U], t: Source[T, V]): Source[NEL[S] Ior NEL[T], NotUsed] = ???
  protected[join] def someLeftIorSomeRight[R, S, T, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, NEL[S] Ior NEL[T], NotUsed] = ???
  protected[join] def someLeftIorSomeRight[R, S, T, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, NEL[S] Ior NEL[T], NotUsed] = someLeftIorSomeRight(t, s).map(_.swap)


  /* assymmetric instances */

  protected[join] def maybeLeftAndRight[S, T, U, V](s: Source[S, U], t: Source[T, V]): Source[(Option[S], T), NotUsed] = ???
  protected[join] def maybeLeftAndRight[R, S, T, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (Option[S], T), NotUsed] = ???
  protected[join] def maybeLeftAndRight[R, S, T, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (Option[S], T), NotUsed] = ???

  protected[join] def leftAndMaybeRight[S, T, U, V](s: Source[S, U], t: Source[T, V]): Source[(S, Option[T]), NotUsed] = ???
  protected[join] def leftAndMaybeRight[R, S, T, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (S, Option[T]), NotUsed] = ???
  protected[join] def leftAndMaybeRight[R, S, T, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (S, Option[T]), NotUsed] = ???


  protected[join] def maybeLeftSomeRight[S, T, U, V](s: Source[S, U], t: Source[T, V]): Source[(Option[S], NEL[T]), NotUsed] = ???
  protected[join] def maybeLeftSomeRight[R, S, T, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (Option[S], NEL[T]), NotUsed] = ???
  protected[join] def maybeLeftSomeRight[R, S, T, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (Option[S], NEL[T]), NotUsed] = ???

  protected[join] def someLeftMaybeRight[S, T, U, V](s: Source[S, U], t: Source[T, V]): Source[(NEL[S], Option[T]), NotUsed] = ???
  protected[join] def someLeftMaybeRight[R, S, T, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (NEL[S], Option[T]), NotUsed] = ???
  protected[join] def someLeftMaybeRight[R, S, T, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (NEL[S], Option[T]), NotUsed] = ???


  protected[join] def leftIorSomeRight[S, T, U, V](s: Source[S, U], t: Source[T, V]): Source[S Ior NEL[T], NotUsed] = ???
  protected[join] def leftIorSomeRight[R, S, T, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, S Ior NEL[T], NotUsed] = ???
  protected[join] def leftIorSomeRight[R, S, T, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, S Ior NEL[T], NotUsed] = ???

  protected[join] def someLeftIorRight[S, T, U, V](s: Source[S, U], t: Source[T, V]): Source[NEL[S] Ior T, NotUsed] = ???
  protected[join] def someLeftIorRight[R, S, T, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, NEL[S] Ior T, NotUsed] = ???
  protected[join] def someLeftIorRight[R, S, T, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, NEL[S] Ior T, NotUsed] = ???


  protected[join] def leftAnyRight[S, T, U, V](s: Source[S, U], t: Source[T, V]): Source[(S, List[T]), NotUsed] = ???
  protected[join] def leftAnyRight[R, S, T, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (S, List[T]), NotUsed] = ???
  protected[join] def leftAnyRight[R, S, T, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (S, List[T]), NotUsed] = ???

  protected[join] def anyLeftAndRight[S, T, U, V](s: Source[S, U], t: Source[T, V]): Source[(List[S], T), NotUsed] = ???
  protected[join] def anyLeftAndRight[R, S, T, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (List[S], T), NotUsed] = ???
  protected[join] def anyLeftAndRight[R, S, T, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (List[S], T), NotUsed] = ???


  protected[join] def leftSomeRight[S, T, U, V](s: Source[S, U], t: Source[T, V]): Source[(S, NEL[T]), NotUsed] = ???
  protected[join] def leftSomeRight[R, S, T, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (S, NEL[T]), NotUsed] = ???
  protected[join] def leftSomeRight[R, S, T, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (S, NEL[T]), NotUsed] = ???

  protected[join] def someLeftAndRight[S, T, U, V](s: Source[S, U], t: Source[T, V]): Source[(NEL[S], T), NotUsed] = ???
  protected[join] def someLeftAndRight[R, S, T, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (NEL[S], T), NotUsed] = ???
  protected[join] def someLeftAndRight[R, S, T, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (NEL[S], T), NotUsed] = ???


  protected[join] def anyLeftSomeRight[S, T, U, V](s: Source[S, U], t: Source[T, V]): Source[(List[S], NEL[T]), NotUsed] = ???
  protected[join] def anyLeftSomeRight[R, S, T, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (List[S], NEL[T]), NotUsed] = ???
  protected[join] def anyLeftSomeRight[R, S, T, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (List[S], NEL[T]), NotUsed] = ???

  protected[join] def someLeftAnyRight[S, T, U, V](s: Source[S, U], t: Source[T, V]): Source[(NEL[S], List[T]), NotUsed] = ???
  protected[join] def someLeftAnyRight[R, S, T, U, V](s: Source[S, U], t: Flow[R, T, V]): Flow[R, (NEL[S], List[T]), NotUsed] = ???
  protected[join] def someLeftAnyRight[R, S, T, U, V](s: Flow[R, S, U], t: Source[T, V]): Flow[R, (NEL[S], List[T]), NotUsed] = ???
}