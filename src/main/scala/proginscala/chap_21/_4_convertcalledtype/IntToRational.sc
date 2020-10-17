class Rational(n: Int, d: Int) {
  require(d != 0)

  private val g = gcd(n.abs, d.abs)
  val numer = n / g
  val denom = d / g

  def this(n: Int) = this(n, 1)

  def +(that: Rational): Rational = new Rational(numer * that.denom + that.numer * denom, denom * that.denom)

  def +(that: Int): Rational = new Rational(this.numer + this.denom * that, this.denom)

  override def toString = n + "/" + d

  private def gcd(a: Int, b: Int): Int =
    if (b == 0) a else gcd(b, a % b)
}

val oneHalf = new Rational(1, 2)
oneHalf + oneHalf
oneHalf + 1

implicit def intToRational(x: Int) = new Rational(x, 1)

1 + oneHalf

