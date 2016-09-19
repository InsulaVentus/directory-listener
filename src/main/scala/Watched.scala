import java.nio.file.{Files, Path, WatchKey}

import scala.collection.JavaConverters._

/**
  * A 'watched' directory.
  */
case class Watched(directory: Path) {

  /**
    * Handle changes to this directory.
    */
  def onChange(watchKey: WatchKey) = {
    watchKey.pollEvents().asScala.foreach { event =>
      val context = directory.resolve(event.context().asInstanceOf[Path])
      println(s"Event kind: '${event.kind()}', Event context: '$context', Is directory? ${Files.isDirectory(context)}")
    }
  }
}
