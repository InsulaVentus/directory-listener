package listenable

import java.nio.file.WatchEvent.Kind
import java.nio.file.{Path, StandardWatchEventKinds, WatchKey, WatchService}

import other.Events

import scala.collection.JavaConverters._

class Log(directory: Path, watchService: WatchService) extends Listenable(directory) {

  override def notify(watchKey: WatchKey): Unit = {
    println(s"Queue size = ${Events.queue.size()}")

    watchKey.pollEvents().asScala.foreach { event =>
      val context = directory.resolve(event.context().asInstanceOf[Path])

      val kind = event.kind()

      kind match {
        case StandardWatchEventKinds.ENTRY_CREATE =>
          println(s"log handler create $context")
          Events.queue.add(s"log handler create $context")

        case StandardWatchEventKinds.ENTRY_MODIFY =>
          println(s"log handler modify $context")
          Events.queue.add(s"log handler modify $context")

        case _ =>
          println(s"log handler delete $context")
          Events.queue.add(s"log handler delete $context")
      }
    }
  }

  def stopListening() = watchService.close() //TODO: When calling this the Listener listening to this will throw a java.nio.file.ClosedWatchServiceException

  override def get(): WatchKey = watchService.take()
}

object Log {

  val CreateModifyDelete: Array[Kind[_]] = Array(
    StandardWatchEventKinds.ENTRY_CREATE,
    StandardWatchEventKinds.ENTRY_MODIFY,
    StandardWatchEventKinds.ENTRY_DELETE
  )

  def apply(directory: Path, watchService: WatchService): Log = {
    directory.register(watchService, CreateModifyDelete)
    new Log(directory, watchService)
  }
}
