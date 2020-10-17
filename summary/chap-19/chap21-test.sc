object NumInt {

}

class NumInt(val num: Int) {

}

object NumString {
  implicit def Str2Int(numString: NumString): NumInt = {
    val num = numString.num match {
      case "One" => 1
      case _ => 0
    }

    new NumInt(num)
  }
}

class NumString(val num: String) {

}

def printInt(numInt: NumInt) = {
  println(numInt.num)
}

val numString = new NumString("One")
printInt(numString)

implicit def double2Int(double: Double) = double.toInt

def fun1 = {
  def fun2 = {
    def fun3 = {
      val int: Int = 3.5
    }
  }
}

val double: Double = 1

case class CaseClass() {
  val a = 1
}

CaseClass().a

CaseClass() match {
  case CaseClass() => 1
}

case class Rectangle(width: Int, height: Int)

implicit class RectangleMaker(width: Int) {
  def x(height: Int) = Rectangle(width, height)
}

1 x 3

val a = 1
val a = 2

object Class0 {
  implicit val c = 1
  implicit val d = '3'
}

object Class1 {
  def fun1()(implicit a: Int, b: Char) = {
    println(a)
    println(b)
  }
}

class Class2 {

  import Class0._

  Class1.fun1()
}

new Class2

def maxListImpParm[T](elements: List[T])(implicit ordering: Ordering[T]): T =
  elements match {
    case List() =>
      throw new IllegalArgumentException("empty list!")
    case List(x) => x
    case x :: rest =>
      val maxRest = maxListImpParm(rest)(ordering)
      if (ordering.gt(x, maxRest)) x
      else maxRest
  }

case class MyClass(a: Int)
maxListImpParm(List(1, 2, 3))
