import java.nio.file._
import java.util.concurrent._

import callable.Diver
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSuite, Matchers}

import scala.collection.mutable

@RunWith(classOf[JUnitRunner])
class DiveTest extends FunSuite with Matchers {

  val TenSeconds: Long = 10000000000L

  //  ignore("should timeout after 5 seconds when dive takes 10 seconds") {
  //    val singleThreadExecutor: ExecutorService = Executors.newSingleThreadExecutor()
  //    val future: Future[Try[WatchKey]] = singleThreadExecutor.submit(
  //      new Dive(Paths.get(""),
  //        FileSystems.getDefault.newWatchService(),
  //        10000000000L)
  //    )
  //
  //    assertThrows[TimeoutException] {
  //      future.get(5, TimeUnit.SECONDS)
  //    }
  //  }

  //  ignore("should get future after 10 seconds") {
  //    val singleThreadExecutor: ExecutorService = Executors.newSingleThreadExecutor()
  //    val future: Future[Try[WatchKey]] = singleThreadExecutor.submit(
  //      new Dive(Paths.get(""),
  //        FileSystems.getDefault.newWatchService(),
  //        10000000000L)
  //    )
  //    val triedKey: Try[WatchKey] = future.get(11, TimeUnit.SECONDS)
  //    triedKey shouldBe Failure(null)
  //  }

  //  test("should find audit directory") {
  //    val diveSpot: Path = Paths.get("tmp")
  //    Files.createDirectory(diveSpot)
  //
  //    val watchService: WatchService = FileSystems.getDefault.newWatchService()
  //    val expectedWatchKey: WatchKey = diveSpot.register(
  //      watchService,
  //      StandardWatchEventKinds.ENTRY_CREATE,
  //      StandardWatchEventKinds.ENTRY_MODIFY,
  //      StandardWatchEventKinds.ENTRY_DELETE
  //    )
  //
  //    Files.createDirectory(Paths.get("tmp/logs"))
  //    Files.createDirectory(Paths.get("tmp/logs/audit"))
  //
  //    val singleThreadExecutor: ExecutorService = Executors.newSingleThreadExecutor()
  //    val future: Future[Try[WatchKey]] = singleThreadExecutor.submit(
  //      new Dive(Paths.get("tmp"),
  //        watchService,
  //        10000000000L)
  //    )
  //
  //    future.get(11, TimeUnit.SECONDS).get shouldBe expectedWatchKey
  //  }

  ignore("dives to the bottom of the sea") {
    val jacquesCousteau: Diver = new Diver(Paths.get("tmp"), mutable.Stack("logs", "audit"), System.nanoTime(), TenSeconds)
    val singleThreadExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    val bottomReached: Future[Option[Path]] = singleThreadExecutor.submit(jacquesCousteau)

    val maybeBottom: Option[Path] = bottomReached.get()
    maybeBottom.isDefined shouldBe true
    maybeBottom.get shouldBe Paths.get("tmp/logs/audit")
  }

  ignore("times out before the bottom of the sea") {
    val jacquesCousteau: Diver = new Diver(Paths.get("tmp"), mutable.Stack("logs", "audit2"), System.nanoTime(), TenSeconds)
    val singleThreadExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    val bottomReached: Future[Option[Path]] = singleThreadExecutor.submit(jacquesCousteau)

    val maybeBottom: Option[Path] = bottomReached.get()
    maybeBottom.isEmpty shouldBe true
  }

  test("dives to the bottom of the sea when the bottom of the sea drops as we go along") {
    val jacquesCousteau: Diver = new Diver(Paths.get("tmp2"), mutable.Stack("logs", "audit", "foo"), System.nanoTime(), TenSeconds)
    val singleThreadExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    val bottomReached: Future[Option[Path]] = singleThreadExecutor.submit(jacquesCousteau)

    Thread.sleep(3000)

    Files.createDirectory(Paths.get("tmp2/logs"))

    Thread.sleep(1500)

    Files.createDirectory(Paths.get("tmp2/logs/audit"))

    Thread.sleep(1500)

    Files.createDirectory(Paths.get("tmp2/logs/audit/foo"))

    val maybeBottom: Option[Path] = bottomReached.get()
    maybeBottom.isDefined shouldBe true
    maybeBottom.get shouldBe Paths.get("tmp2/logs/audit/foo")
  }
}
