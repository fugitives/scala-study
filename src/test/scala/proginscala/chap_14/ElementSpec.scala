package proginscala.chap_14

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import proginscala.chap_14.Element.element

class ElementSpec extends AnyFlatSpec with Matchers {
  "A UniformElement" should "have a width equal to the passed value" in {
    val ele = element('x', 3, 2)
    ele.width should be(2)
  }

  it should "have a height equal to the passed value" in {
    val ele = element('x', 3, 2)
    ele.height should be(3)
  }

  it should "throw an IAE if passed a negative width" in {
    an[IllegalArgumentException] should be thrownBy {
      element('x', -2, 3)
    }
  }
}
