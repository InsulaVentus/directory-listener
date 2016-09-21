package runnable

import listenable.Listenable

import scala.annotation.tailrec
import scala.util.Success

/**
  * Listen for changes
  */
class Listener(listenable: Listenable, continueListening: Boolean) extends Runnable {

  override def run() = {
    listen(listenable, continueListening)
  }

  @tailrec
  private def listen(listenable: Listenable, continueListening: Boolean): Unit = {
    if (continueListening) {
      println(s"Thread '${Thread.currentThread().getName}' is listening to ${listenable.getName}")
      listenable.get() match {
        case Success(s) =>
          listenable.notify(s)
          listen(listenable, s.reset())
        case _ =>
          println(s"Thread '${Thread.currentThread().getName}' will no longer listen to ${listenable.getName}")
      }
    }
  }
}

object Listener {
  def apply(listenable: Listenable, continueListening: Boolean): Listener = new Listener(listenable, continueListening)
}
