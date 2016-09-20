package runnable

import java.nio.file.WatchKey

import listenable.Listenable

import scala.annotation.tailrec

/**
  * Listen for changes
  */
class Listener(listenTo: Listenable, continueListening: Boolean) extends Runnable {

  override def run() = {
    listen(listenTo, continueListening)
  }

  @tailrec
  private def listen(listenTo: Listenable, continueListening: Boolean): Unit = {
    if (continueListening) {
      println(s"Thread '${Thread.currentThread().getName}' is listening to ${listenTo.getName}")
      val watchKey: WatchKey = listenTo.get() //TODO: Handle java.nio.file.ClosedWatchServiceException
      listenTo.notify(watchKey)
      listen(listenTo, watchKey.reset())
    }
  }
}

object Listener {
  def apply(listenTo: Listenable, continueListening: Boolean): Listener = new Listener(listenTo, continueListening)
}
