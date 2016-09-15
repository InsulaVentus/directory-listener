package other

import java.nio.file.{WatchKey, WatchService}

import scala.annotation.tailrec

/**
  * Listen for changes
  */
object Listener2 {

  @tailrec
  def listen(callback: (WatchKey) => Unit, watchService: WatchService, continueListening: Boolean): Unit = {
    if (continueListening) {
      println("Listening..")
      val watchKey: WatchKey = watchService.take()
      callback(watchKey)
      listen(callback, watchService, watchKey.reset())
    }
  }
}
