import java.util.concurrent.TimeUnit

import callable.Dive
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ASyncTest extends FunSuite {

  test("testingTheTest") {
    new Thread(new Dive(10, TimeUnit.SECONDS), "diving").start()


  }

}
