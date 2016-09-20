package listenable

import java.nio.file.{Path, WatchKey}

abstract class Listenable(directory: Path) {

  def getName: String = {
    directory.getFileName.toString
  }

  def notify(watchKey: WatchKey): Unit

  def get(): WatchKey
}
