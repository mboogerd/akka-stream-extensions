package com.github.mboogerd.streams.sorted.join

import com.github.mboogerd.streams.sorted.join.JoinInstances._

/**
  *
  */
trait CardinalityInstances {


  class MaybeToMaybe[S, T] extends OneToOneInnerJoin[S, T] with OneIorOneOuterJoin[S, T] with OneToMaybeLeftJoin[S, T] with MaybeToOneRightJoin[S, T]

  class MaybeToOne[S, T] extends OneToOneInnerJoin[S, T] with MaybeToOneOuterJoin[S, T] with OneToOneLeftJoin[S, T] with MaybeToOneRightJoin[S, T]

  class MaybeToAny[S, T] extends OneToSomeInnerJoin[S, T] with OneIorSomeOuterJoin[S, T] with OneToAnyLeftJoin[S, T] with MaybeToSomeRightJoin[S, T]

  class MaybeToSome[S, T] extends OneToSomeInnerJoin[S, T] with MaybeToSomeOuterJoin[S, T] with OneToSomeLeftJoin[S, T] with MaybeToSomeRightJoin[S, T]



  class OneToMaybe[S, T] extends MaybeToOne[T, S]

  class OneToOne[S, T] extends OneToOneInnerJoin[S, T] with OneToOneOuterJoin[S, T] with OneToOneLeftJoin[S, T] with OneToOneRightJoin[S, T]

  class OneToAny[S, T] extends OneToSomeInnerJoin[S, T] with OneToSomeOuterJoin[S, T] with OneToAnyLeftJoin[S, T] with OneToSomeRightJoin[S, T]

  class OneToSome[S, T] extends OneToSomeInnerJoin[S, T] with OneToSomeOuterJoin[S, T] with OneToSomeLeftJoin[S, T] with OneToSomeRightJoin[S, T]



  class AnyToMaybe[S, T] extends MaybeToAny[T, S]

  class AnyToOne[S, T] extends OneToAny[T, S]

  class AnyToAny[S, T] extends SomeToSomeInnerJoin[S, T] with SomeIorSomeOuterJoin[S, T] with SomeToAnyLeftJoin[S, T] with AnyToSomeRightJoin[S, T]

  class AnyToSome[S, T] extends SomeToSomeInnerJoin[S, T] with AnyToSomeOuterJoin[S, T] with SomeToSomeLeftJoin[S, T] with AnyToSomeRightJoin[S, T]



  class SomeToMaybe[S, T] extends MaybeToSome[T, S]

  class SomeToOne[S, T] extends OneToSome[T, S]

  class SomeToAny[S, T] extends AnyToSome[T, S]

  class SomeToSome[S, T] extends SomeToSomeInnerJoin[S, T] with SomeToSomeOuterJoin[S, T] with SomeToSomeLeftJoin[S, T] with SomeToSomeRightJoin[S, T]

}

object JoinDefinitionInstances extends CardinalityInstances
