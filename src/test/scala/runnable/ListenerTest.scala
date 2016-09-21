package runnable

import java.nio.file._

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSuite, Matchers}

import scala.collection.JavaConverters._

/**
  * https://twitter.github.io/scala_school/concurrency.html
  * http://www.scala-lang.org/api/rc2/scala/actors/MessageQueue.html
  * http://programmers.stackexchange.com/questions/81003/how-to-explain-why-multi-threading-is-difficult
  * http://docs.scala-lang.org/overviews/core/futures.html
  *
  * Setup:
  * 1. Create directory "tmpDir/"
  * 2. Create "tmpDir/1/" and "tmpDir/2/"
  * 3. Create "tmpDir/1/log" and "tmpDir/2/log"
  * 4. Start watching "tmpDir/" and "tmpDir/2/"
  *
  * Test-1:
  * 5. Append a line to "tmpDir/2/log"
  * 6. Assert that the line is registered
  *
  * Test-2:
  * 7. Create "tmpDir/3/" and "tmpDir/3/log"
  * 8. Assert that we stop watching "tmpDir/2/"
  *
  * Test-3
  * 9. Append a line to "tmpDir/3/log"
  * 10. Assert that the line is registered
  */
@RunWith(classOf[JUnitRunner])
class ListenerTest extends FunSuite with Matchers {

  ignore("testing") {

    val parent = Files.createDirectory(Paths.get("tmpDir"))

    val subDirs = Array[Path](
      Files.createDirectory(Paths.get("tmpDir/1")),
      Files.createDirectory(Paths.get("tmpDir/2"))
    )

    val files = Array[Path](
      Files.createFile(Paths.get("tmpDir/1/log")),
      Files.createFile(Paths.get("tmpDir/2/log"))
    )

    val watchService: WatchService = FileSystems.getDefault.newWatchService()

    val parentWatchKey: WatchKey = parent.register(
      watchService,
      StandardWatchEventKinds.ENTRY_CREATE,
      StandardWatchEventKinds.ENTRY_MODIFY,
      StandardWatchEventKinds.ENTRY_DELETE
    )

    val subDirWatchKeys: Seq[WatchKey] = subDirs.map(_.register(
      watchService,
      StandardWatchEventKinds.ENTRY_CREATE,
      StandardWatchEventKinds.ENTRY_MODIFY,
      StandardWatchEventKinds.ENTRY_DELETE
    ))


//        val future: Future[Any] = Future {
    //          Listener.listen(runnable.ListenerTest.eventHandler, watchService, continueListening = true)
//        }


    //    val listeningThread: Thread = new Thread(new Listener(parent, runnable.ListenerTest.eventHandler, watchService, continueListening = true))
//    listeningThread.start()

    println("Listening in progress")
//    listeningThread.wait()
    Thread.sleep(20000)


    true
  }

  /**
    * Assert that a WatchKey matches a given file (Path) and kind (WatchEvent.Kind[Path]).
    */
  def assertPathAndEvent(watchKey: WatchKey, expectedPath: Path, expectedEventKind: WatchEvent.Kind[Path]) = {
    watchKey.pollEvents().asScala.foreach { event =>
      assert(event.kind() == expectedEventKind)
      assert(event.context() == expectedPath.getFileName)
    }
  }
}

object ListenerTest {
  def eventHandler(watchKey: WatchKey) = {
    watchKey.pollEvents().asScala.foreach { event =>
      println(s"Event kind: '${event.kind()}', Event context: '${event.context()}'")
    }
  }
}


//    val subDirWatchKeys: Seq[WatchKey] = subDirs.map(_.register(
//      watchService,
//      StandardWatchEventKinds.ENTRY_CREATE,
//      StandardWatchEventKinds.ENTRY_MODIFY,
//      StandardWatchEventKinds.ENTRY_DELETE
//    ))

//    val watchKey: WatchKey = watchService.poll(10, TimeUnit.SECONDS)
//    val watchKey: WatchKey = watchService.take()
//    assertPathAndEvent(watchKey, Paths.get("tmpDir/2/log"), StandardWatchEventKinds.ENTRY_MODIFY)
//    watchKey.reset()


//    watchService.close()
//
//    files.foreach(Files.delete)
//    subDirs.foreach(Files.delete)
//    Files.delete(parent)
