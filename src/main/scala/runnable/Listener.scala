package runnable

import java.nio.file.{Path, WatchService, WatchKey}

import scala.annotation.tailrec

/**
  * Listen for changes
  */
class Listener(listenTo: Path, callbackOnEvent: (Path, WatchKey, WatchService) => Unit, watchService: WatchService, continueListening: Boolean) extends Runnable {

  override def run() = {
    listen(listenTo, callbackOnEvent, watchService, continueListening)
  }

  @tailrec
  private def listen(listenTo: Path, callbackOnEvent: (Path, WatchKey, WatchService) => Unit, watchService: WatchService, continueListening: Boolean): Unit = {
    if (continueListening) {
      println(s"Thread '${Thread.currentThread().getName}' is listening to ${listenTo.getFileName.toString}")
      val watchKey: WatchKey = watchService.take()
      callbackOnEvent(listenTo, watchKey, watchService)
      listen(listenTo, callbackOnEvent, watchService, watchKey.reset())
    }
  }
}
