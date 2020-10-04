case class MyClass(value: Int) {
  def ::(that: MyClass) = {
    MyClass(value - that.value)
  }
}

val value1 = MyClass(1)
val value2 = MyClass(2)


//val value1 :: value2 = value1 :: value2
//
//List(1, List(2)).flatten
