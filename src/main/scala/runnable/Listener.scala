package runnable

import java.nio.file.{Path, WatchService, WatchKey}

import scala.annotation.tailrec

/**
  * Listen for changes
  */
class Listener(listenTo: Path, callbackOnEvent: (Path, WatchKey) => Unit, watchService: WatchService, continueListening: Boolean) extends Runnable {

  override def run() = {
    listen(listenTo, callbackOnEvent, watchService, continueListening)
  }

  @tailrec
  private def listen(listenTo: Path, callbackOnEvent: (Path, WatchKey) => Unit, watchService: WatchService, continueListening: Boolean): Unit = {
    if (continueListening) {
      println(s"The thread '${Thread.currentThread().getName}' is listening...")
      val watchKey: WatchKey = watchService.take()
      callbackOnEvent(listenTo, watchKey)
      listen(listenTo, callbackOnEvent, watchService, watchKey.reset())
    }
  }
}
