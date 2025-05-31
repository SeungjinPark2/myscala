package example

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class CalculatorTest extends AnyFunSpec with Matchers {
  describe("A Calculator") {
    it("should add two positive numbers") {
      val calc = new Calculator()
      calc.add(1, 2) shouldBe 3
    }

    it("should add negative numbers") {
      val calc = new Calculator()
      calc.add(-1, -2) shouldBe -3
    }
  }
}
