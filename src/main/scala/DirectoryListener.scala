import java.nio.file._

import listenable.{Component, Log}
import other.Events.queue
import runnable.Listener

object DirectoryListener {

  def main(args: Array[String]) = {

    //START PROCEDURE:
    val aLog: Log = Log(Paths.get("/Users/insulaventus/tmp/logs/audit"), FileSystems.getDefault.newWatchService(), queue)
    val aComponent: Component = Component(Paths.get("/Users/insulaventus/tmp"), FileSystems.getDefault.newWatchService(), aLog)

    new Thread(Listener(aComponent, continueListening = true), "listen-to-component").start()
    new Thread(Listener(aLog, continueListening = true), "listen-to-log").start()
  }
}
