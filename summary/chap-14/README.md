# Chapter 14. 단언문과 테스트 

## 14.1 Assertions
* assert 구문 사용
* false 면 AssertionError
* AssertionError 의 toString 이 힌트다

```scala
  def above(that: Element): Element = {
    val widenThis = this widen that.width
    val widenThat = that widen this.width
    assert(widenThis.width == widenThat.width)
    element(widenThis.contents ++ widenThat.contents)
  }
```

* ensuring helper 
* implicit 변환을 통해 어떤 타입이건 대입 가능
* cond: A => Boolean 이놈은 predicate function
* JVM -ea -da 를 통해 켜고 끔이 가능하다
```scala
  implicit final class Ensuring[A](private val self: A) extends AnyVal {
    def ensuring(cond: Boolean): A = { assert(cond); self }
    def ensuring(cond: Boolean, msg: => Any): A = { assert(cond, msg); self }
    def ensuring(cond: A => Boolean): A = { assert(cond(self)); self } // 여기 봐주세요
    def ensuring(cond: A => Boolean, msg: => Any): A = { assert(cond(self), msg); self }
  }
```
```scala
  def widen(w: Int): Element = {
    if (w <= width) this
    else {
      val left = element(' ', (w - width) / 2, height)
      val right = element(' ', w - width - left.width, height)
      left beside this beside right
    } ensuring(w <= _.width)
  }
```

## 14.2 Unit testing in Scala
* 귀찮아서 sbt 사용함
* scalatest
```scala
libraryDependencies ++= {
  val scalaTestVersion = "3.2.2"
  Seq(
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
    "org.scalatest" %% "scalatest-mustmatchers" % scalaTestVersion % "test"
  )
}
```
* Test 가 여러개 모인것이 Suite 다
* 여러 테스트를 지원하기 위해 여러 Suite 가 있다.
* Style Trait, Mixin Trait
* FunSuite 는 Style Trait (test method 는 FunSuite 에 정의 되어 있음)

## 14.3 Informative failure reports
* DiagrammedAssertions
```scala
  test("assert list") {
    org.scalatest.diagrams.Diagrams.assert(List(1,2,3).contains(4))
  }

org.scalatest.diagrams.Diagrams.assert(List(1,2,3).contains(4))
                                       |    | | |  |        |
                                       |    1 2 3  false    4
                                       List(1, 2, 3)
```

* assertResult
```scala
  test("assert result") {
    assertResult(2) {
      3
    }
  }

Expected 2, but got 3
```

* assertThrows 예외 상황이어야 한다
```scala
  test("assert throws") {
    assertThrows[IllegalArgumentException] {
      element('x', -2, 3)
    }
  }
```

* intercept 예외를 가지고 있다.
```scala
  test("intercept") {
    val caught = intercept[ArithmeticException] {
      1 / 0
    }
    assert(caught.getMessage == "/ by zero")
  }
```

## 14.4 Tests as specifications
* BDD(behavior-driven-development) test style
* specifier clause (명세 절) 을 이용한 테스트 작성
* Subject + should(must or can) --- in {}
* 최근에 쓴 Subject 는 it 으로 대체 가능 
* Matchers trait 을 통해 DSL(domain-specific language) 지원(사람이 읽기 좋다) 
* 이해하기 쉽다.
* FeatureSpec
```scala
class TVSetSpec extends AnyFeatureSpec with GivenWhenThen {
  Feature("Tv power button") {
    Scenario("User presses power button when TV is off") {
      Given("a TV set that is switched off")
      When("the power button is pressed")
      Then("the TV should switch on")
      pending
    }
  }
}
```

## 14.5 Property-based testing
* ElementSpec2
```scala
class ElementSpec2 extends AnyWordSpec with Matchers with ScalaCheckPropertyChecks {
  "elem result" must {
    "have passed width" in {
      forAll { w: Int =>
        whenever(w > 0) {
          element('x', 3, w).width must equal(w)
        }
      }
    }
  }
}
```

## 14.6 Organizing and running tests
* sbt 로 되어있음 그냥 실행 하자!
