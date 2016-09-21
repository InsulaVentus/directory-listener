package listenable

import java.nio.file.WatchEvent.Kind
import java.nio.file.{Path, StandardWatchEventKinds, WatchKey, WatchService}
import java.util

import scala.collection.JavaConverters._
import scala.util.Try

class Log(directory: Path, watchService: WatchService, queue: util.Queue[String]) extends Listenable(directory) {

  override def notify(watchKey: WatchKey): Unit = {
    println(s"Queue size = ${queue.size()}")

    watchKey.pollEvents().asScala.foreach { event =>
      val context = directory.resolve(event.context().asInstanceOf[Path])

      val kind = event.kind()

      kind match {
        case StandardWatchEventKinds.ENTRY_CREATE =>
          println(s"log handler create $context")
          queue.offer(s"log handler create $context")

        case StandardWatchEventKinds.ENTRY_MODIFY =>
          println(s"log handler modify $context")
          queue.offer(s"log handler modify $context")

        case _ =>
          println(s"log handler delete $context")
          queue.offer(s"log handler delete $context")
      }
    }
  }

  def stopListening() = watchService.close()

  override def get(): Try[WatchKey] = Try(watchService.take())
}

object Log {

  val CreateModifyDelete: Array[Kind[_]] = Array(
    StandardWatchEventKinds.ENTRY_CREATE,
    StandardWatchEventKinds.ENTRY_MODIFY,
    StandardWatchEventKinds.ENTRY_DELETE
  )

  def apply(directory: Path, watchService: WatchService, queue: util.Queue[String]): Log = {
    directory.register(watchService, CreateModifyDelete)
    new Log(directory, watchService, queue)
  }
}
