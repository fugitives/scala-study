object JennyFoo {
  implicit def foo(a: String) = println("String 메소드라구~")

  implicit def foo(a: Any) = println("Any 메소드라구~")
}

import JennyFoo._

foo("나와라 foo 메소드!")
foo(null)
foo(1)
