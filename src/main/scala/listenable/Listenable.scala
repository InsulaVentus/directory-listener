package listenable

import java.nio.file.{Path, WatchKey}

import scala.util.Try

abstract case class Listenable(directory: Path) {

  def getName: String = {
    directory.toAbsolutePath.toString
  }

  def notify(watchKey: WatchKey): Unit

  def get(): Try[WatchKey]
}
