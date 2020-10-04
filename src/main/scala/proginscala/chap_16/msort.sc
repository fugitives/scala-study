import scala.util.Random

def msort[A](less: (A, A) => Boolean)(list: List[A]): List[A] = {
  def merge(xs: List[A], ys: List[A]): List[A] =
    (xs, ys) match {
      case (Nil, _) => ys
      case (_, Nil) => xs
      case (x :: xs1, y :: ys1) =>
        if (less(x, y))
          x :: merge(xs1, ys)
        else
          y :: merge(xs, ys1)
    }

  val len = list.length
  if (len <= 1) list
  else {
    val (left, right) = list.splitAt(len / 2)
    merge(msort(less)(left), msort(less)(right))
  }
}

val list = Random.shuffle((0 until 10).toList)
msort((x: Int, y: Int) => x < y)(list)
