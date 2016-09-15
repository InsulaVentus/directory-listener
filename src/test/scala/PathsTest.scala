import java.nio.file._

import org.scalatest.{FunSpec, Matchers}

import scala.collection.JavaConverters._

class PathsTest extends FunSpec with Matchers {

  it("terminology") {

    val watcher: WatchService = FileSystems.getDefault.newWatchService()

    Paths
      .get("C:/cygwin64/home/cc32403/data/listen-to-this")
      .register(
        watcher,
        StandardWatchEventKinds.ENTRY_CREATE,
        StandardWatchEventKinds.ENTRY_MODIFY,
        StandardWatchEventKinds.ENTRY_DELETE
      )

    //    val watchKey: WatchKey = watcher.poll(120, SECONDS)
    val watchKey: WatchKey = watcher.take()


    watchKey.pollEvents().asScala.foreach { event =>
      println(s"Kind of event: ${event.kind()}, Event context: ${event.context()}")
    }
    watchKey.reset()
  }
}
