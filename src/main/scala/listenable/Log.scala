package listenable

import java.nio.file.{Path, StandardWatchEventKinds, WatchKey, WatchService}

import scala.collection.JavaConverters._

class Log(directory: Path, watchService: WatchService) extends Listenable {

  override def getName: String = {
    directory.getFileName.toString
  }

  override def notify(watchKey: WatchKey): Unit = {
    watchKey.pollEvents().asScala.foreach { event =>
      val context = directory.resolve(event.context().asInstanceOf[Path])

      val kind = event.kind()

      kind match {
        case StandardWatchEventKinds.ENTRY_CREATE => println(s"log handler create $context")
        case StandardWatchEventKinds.ENTRY_MODIFY => println(s"log handler modify $context")
        case _ => println(s"log handler delete $context")
      }
    }
  }
}
