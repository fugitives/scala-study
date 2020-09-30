package proginscala.chap_14

import org.scalatest.funsuite.AnyFunSuite
import proginscala.chap_14.Element.element
class ElementSuite extends AnyFunSuite {

  test("elem result should have passed width") {
    val ele = element('x', 3, 2)
    assert(ele.width == 2)
  }

  test("elem result should not have passed width") {
    val ele = element('x', 3, 2)
    assert(ele.width == 3)
  }

  test("assert list") {
    org.scalatest.diagrams.Diagrams.assert(List(1,2,3).contains(4))
  }

  test("assert result") {
    assertResult(2) {
      3
    }
  }

  test("assert throws") {
    assertThrows[IllegalArgumentException] {
      element('x', -2, 3)
    }
  }

  test("intercept") {
    val caught = intercept[ArithmeticException] {
      1 / 0
    }
    assert(caught.getMessage == "/ by zero")
  }
}
