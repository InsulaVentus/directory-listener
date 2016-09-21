package listenable

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSuite, Matchers}

@RunWith(classOf[JUnitRunner])
class ComponentTest extends FunSuite with Matchers {

  test("should return false when directory name is empty") {
    Component.isValidDirectoryName("") shouldBe false
  }

  test("should return true when a valid directory name is passed") {
    Component.isValidDirectoryName("35465.445") shouldBe true
  }

  test("should return false when valid directory name ending with '.'") {
    Component.isValidDirectoryName("321321.") shouldBe false
  }

  test("should return false when directory name starts with '.'") {
    Component.isValidDirectoryName(".3.4") shouldBe false
  }

  test("should return false when directory name contains letters") {
    Component.isValidDirectoryName("1.E.4") shouldBe false
  }
}
