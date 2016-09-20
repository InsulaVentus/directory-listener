package runnable

import java.nio.file.{WatchKey, WatchService}

import listenable.Listenable

import scala.annotation.tailrec

/**
  * Listen for changes
  */
class Listener(listenTo: Listenable, watchService: WatchService, continueListening: Boolean) extends Runnable {

  override def run() = {
    listen(listenTo, watchService, continueListening)
  }

  @tailrec
  private def listen(listenTo: Listenable, watchService: WatchService, continueListening: Boolean): Unit = {
    if (continueListening) {
      println(s"Thread '${Thread.currentThread().getName}' is listening to ${listenTo.getName}")
      val watchKey: WatchKey = watchService.take()
      listenTo.notify(watchKey)
      listen(listenTo, watchService, watchKey.reset())
    }
  }
}
