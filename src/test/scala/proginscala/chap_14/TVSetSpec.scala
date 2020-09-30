package proginscala.chap_14

import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec

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
