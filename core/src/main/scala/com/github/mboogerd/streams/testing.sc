import cats.Order

trait MonotonicMorphism[S, T] {
  def apply(s: S): T
}

def f[S: MonotonicMorphism](s: S)

def g[T: MonotonicMorphism](t: T)


class SortedSource[S, C: Order](streamS: Stream[S]) {
  def join[T](streamT: Stream[T])(implicit sToC: MonotonicMorphism[S, C]
                                  , tToC: MonotonicMorphism[T, C]) = {

  }
}

trait Stream[S] {
  def join[T]
}