import java.nio.file.WatchService

import scala.annotation.tailrec
import scala.util.{Success, Try}

class Listener(queue: WatchService, components: Seq[Component]) extends Runnable {

  override def run(): Unit = {
    listen()
  }

  def listen() = {
    @tailrec
    def recursiveListen(continueListening: Boolean): Unit = {
      if (continueListening) {
        println(s"Listening to $queue ...")
        Try(queue.take()) match {

          case Success(watchKey) =>
            components.find(component => component.isComponent(watchKey) || component.isLog(watchKey)) match {

              case Some(component) =>
                component.getListenable(watchKey) match {
                  case Some(listenable) => listenable.handleEvent(watchKey)
                  case _ => watchKey.cancel()
                }
              case _ =>
            }

            recursiveListen(continueListening = true)
          case _ => recursiveListen(continueListening = false)
        }
      }
    }
    recursiveListen(continueListening = true)
  }
}

object Listener {
  def apply(queue: WatchService, components: Seq[Component]): Listener = {
    new Listener(queue, components)
  }
}
