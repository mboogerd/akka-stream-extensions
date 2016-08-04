package com.github.mboogerd.streams.sorted.join

/**
  * A simple marker trait to indicate that a function is monotonic, i.e. order-preserving:
  * a <= b ==> Monotonic(a) <= Monotonic(b)
  */
trait Monotonic[S, T] extends (S ⇒ T)
