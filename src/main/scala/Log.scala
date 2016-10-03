import java.nio.file.{Path, StandardWatchEventKinds, WatchKey, WatchService}

import scala.collection.JavaConverters._

case class Log(directory: Path, queue: WatchService) extends Listenable {

  override def handleEvent(watchKey: WatchKey): Unit = {
    watchKey.pollEvents().asScala.foreach { event =>
      val context = directory.resolve(event.context().asInstanceOf[Path])

      event.kind() match {
        case StandardWatchEventKinds.ENTRY_CREATE =>
          println(s"Create event for: ${this.toString}")

        case StandardWatchEventKinds.ENTRY_MODIFY =>
          println(s"Modify event for: ${this.toString}")

        case StandardWatchEventKinds.ENTRY_DELETE =>
          println(s"Delete event for: ${this.toString}")

        case _ =>
          println(s"Overflow event for: ${this.toString}")
      }
    }
    watchKey.reset()
  }

  override def register(): Log = {
    println(s"Registering log - ${directory.toAbsolutePath.toString}")
    directory.register(queue, CreateModifyDelete)
    this
  }

  override def isComponent(watchKey: WatchKey): Boolean = {
    watchKey.watchable().asInstanceOf[Path].equals(directory)
  }

  override def toString = s"Log(${directory.toAbsolutePath.toString})\n"
}
