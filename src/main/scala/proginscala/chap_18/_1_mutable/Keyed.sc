import proginscala.chap_18._1_mutable._

val m = new MemoKeyed()
println(m.computeKey)

class Time {
  private[this] var h = 12
  private[this] var m = 0

  def hour: Int = h

  def hour_=(x: Int) = {
    h = x
  }

  def minute: Int = m

  def minute_=(x: Int) = {
    m = x
  }
}
