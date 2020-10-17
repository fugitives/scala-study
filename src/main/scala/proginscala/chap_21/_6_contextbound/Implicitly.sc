//def maxList[T](elements: List[T])(implicit ordering: Ordering[T]): T =
//  elements match {
//    case List() => throw new IllegalArgumentException("empty list")
//    case List(x) => x
//    case x :: rest =>
//      val maxRest = maxList(rest)
//      if (ordering.gt(x, maxRest)) x
//      else maxRest
//  }

//def implicitly[T](implicit t: T) = t

//def maxList[T](elements: List[T])(implicit ordering: Ordering[T]): T =
//  elements match {
//    case List() => throw new IllegalArgumentException("empty list")
//    case List(x) => x
//    case x :: rest =>
//      val maxRest = maxList(rest)
//      if (implicitly[Ordering[T]].gt(x, maxRest)) x
//      else maxRest
//  }

def maxList[T: Ordering](elements: List[T]): T =
  elements match {
    case List() => throw new IllegalArgumentException("empty list")
    case List(x) => x
    case x :: rest =>
      val maxRest = maxList(rest)
      if (implicitly[Ordering[T]].gt(x, maxRest)) x
      else maxRest
  }
