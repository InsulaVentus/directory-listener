package listenable

import java.nio.file.WatchKey


trait Listenable {

  def getName: String

  def notify(watchKey: WatchKey): Unit
}
