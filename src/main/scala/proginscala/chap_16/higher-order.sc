import scala.collection.mutable.{ArrayBuffer, ListBuffer}

val oneToFive = (1 to 5).toList
val p: Int => Boolean = _ % 2 == 0

oneToFive filter p

oneToFive partition p

(oneToFive filter p, oneToFive filter (!p(_)))

oneToFive find p

(oneToFive.head /: oneToFive.tail) (_ + _)

oneToFive.tail./:(oneToFive.head) _

oneToFive.reverse.sortWith(_ < _)
List.range(1, 2, 3)

List.fill(2, 2) (0)

val xss = List(List(1), List(2, 3))

(xss :\ List()) (_ ::: _)

var set = Set(1)

set += 2

var float = 0.3

float += 0.1

def test(test: => Unit) = {
  Thread.sleep(3000)
  test
}

test {
  println("hi")
  println("bye")
}

class Test

object Test2 extends Test
