import java.nio.file.{Path, WatchKey, WatchService}


/**
  * A listener which aims at finding a given subdirectory and start listening to it.
  */
class DiveListener(listenTo: Path, callbackOnEvent: (Path, WatchKey, WatchService) => Unit, watchService: WatchService, continueListening: Boolean) extends Runnable {

  override def run(): Unit = {
    listen(listenTo, callbackOnEvent, watchService, continueListening)
  }

  private def listen(listenTo: Path, callbackOnEvent: (Path, WatchKey, WatchService) => Unit, watchService: WatchService, continueListening: Boolean) = {


  }

}
