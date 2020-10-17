package proginscala.chap_20

trait Abstract {
  type T

  def transform(x: T): T

  val initial: T
  var current: T
}

class Concrete extends Abstract {
  type T = String

  def transform(x: T): String = x + x

  val initial = "hi"
  var current: String = initial
}
