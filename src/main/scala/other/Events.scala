package other

import java.util.concurrent.ConcurrentLinkedQueue

object Events {

  var queue: ConcurrentLinkedQueue[String] = new ConcurrentLinkedQueue[String]()

  //  var eventQueue: mutable.Syn[String] = mutable.Queue[String]()

}
