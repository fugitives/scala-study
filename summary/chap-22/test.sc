import scala.collection.mutable.ListBuffer

case class Test(a: Int, b: Int) {
  println("bye")
  def apply(a: Int, b: Int): Test = {
    println(s"a: $a, b: $b")
    Test(1, 2)
  }
}

val test = Test(1, 2)

test match {
  case a Test b => {
    println("hi")
  }
}

val xs = List(1, 2, 3, 4)
val buf = new ListBuffer[Int]
for (x <- xs) buf += x + 1
buf.toList

val ys = 1 :: xs
val zs = 1 :: xs
