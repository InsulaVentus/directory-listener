package other

import java.util.concurrent.ConcurrentLinkedQueue

object Events {

  val queue: ConcurrentLinkedQueue[String] = new ConcurrentLinkedQueue[String]()

  def add(event: String) = {
    queue.add(event)
  }

  def poll(): Option[String] = {
    Option(queue.poll())
  }
}
