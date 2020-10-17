def maxListImpParam[T](elements: List[T])
                      (implicit ordering: Ordering[T]): T =
  elements match {
    case List() => throw new IllegalArgumentException("empty list")
    case List(x) => x
    case x :: rest =>
      val maxRest = maxListImpParam(rest)(ordering)
      if (ordering.gt(x, maxRest)) x
      else maxRest
  }
