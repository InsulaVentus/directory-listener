import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@RunWith(classOf[JUnitRunner])
class ASyncTest extends FunSuite {

  test("testingTheTest") {

    val s = "Hello"
    val aFuture: Future[String] = Future {
      Thread.sleep(5000)
      s + " future!"
    }
    aFuture.onSuccess {
      case msg => println(msg)
    }

    true
  }

}
