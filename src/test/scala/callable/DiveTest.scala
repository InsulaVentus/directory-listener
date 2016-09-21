package callable

import java.nio.file._
import java.util.concurrent._

import common.FileTest
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSuite, Matchers}

import scala.collection.mutable

@RunWith(classOf[JUnitRunner])
class DiveTest extends FunSuite with Matchers {

  val TenSeconds: Long = 10000000000L

  test("dives to the bottom of the sea") {
    val base: Path = Paths.get("tmp")
    Files.createDirectory(base)
    Files.createDirectory(base.resolve("logs"))
    Files.createDirectory(base.resolve("logs/audit"))

    val jacquesCousteau: Diver = new Diver(Paths.get("tmp"), mutable.Stack("logs", "audit"), System.nanoTime(), TenSeconds)
    val singleThreadExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    val bottomReached: Future[Option[Path]] = singleThreadExecutor.submit(jacquesCousteau)

    val maybeBottom: Option[Path] = bottomReached.get()
    maybeBottom.isDefined shouldBe true
    maybeBottom.get shouldBe Paths.get("tmp/logs/audit")

    FileTest.removeRecursive(base)
  }

  test("times out before the bottom of the sea") {
    val base: Path = Paths.get("tmp")
    Files.createDirectory(base)
    Files.createDirectory(base.resolve("logs"))
    Files.createDirectory(base.resolve("logs/audit"))

    val jacquesCousteau: Diver = new Diver(Paths.get("tmp"), mutable.Stack("logs", "audit2"), System.nanoTime(), TenSeconds)
    val singleThreadExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    val bottomReached: Future[Option[Path]] = singleThreadExecutor.submit(jacquesCousteau)

    val maybeBottom: Option[Path] = bottomReached.get()
    maybeBottom.isEmpty shouldBe true

    FileTest.removeRecursive(base)
  }

  test("dives to the bottom of the sea when the bottom of the sea drops as we go along") {
    val base: Path = Paths.get("tmp")
    Files.createDirectory(base)

    val jacquesCousteau: Diver = new Diver(Paths.get("tmp"), mutable.Stack("logs", "audit", "foo"), System.nanoTime(), TenSeconds)
    val singleThreadExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    val bottomReached: Future[Option[Path]] = singleThreadExecutor.submit(jacquesCousteau)

    Thread.sleep(3000)

    Files.createDirectory(base.resolve("logs"))

    Thread.sleep(1500)

    Files.createDirectory(base.resolve("logs/audit"))

    Thread.sleep(1500)

    Files.createDirectory(base.resolve("logs/audit/foo"))

    val maybeBottom: Option[Path] = bottomReached.get()
    maybeBottom.isDefined shouldBe true
    maybeBottom.get shouldBe Paths.get("tmp/logs/audit/foo")

    FileTest.removeRecursive(base)
  }
}
