package proginscala.chap_14

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import proginscala.chap_14.Element.element

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
