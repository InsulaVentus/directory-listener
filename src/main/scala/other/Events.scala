package other

import java.util.concurrent.ConcurrentLinkedQueue

object Events {
  val queue: ConcurrentLinkedQueue[String] = new ConcurrentLinkedQueue[String]()
}
