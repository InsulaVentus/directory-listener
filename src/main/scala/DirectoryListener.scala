import java.nio.file._

import runnable.Listener

import scala.collection.JavaConverters._

object DirectoryListener {

  def main(args: Array[String]) = {
    val watchService: WatchService = FileSystems.getDefault.newWatchService()

    val listenTo: Path = Paths.get("tmpDir")

    listenTo.register(
      watchService,
      StandardWatchEventKinds.ENTRY_CREATE,
      StandardWatchEventKinds.ENTRY_MODIFY,
      StandardWatchEventKinds.ENTRY_DELETE
    )

    val listeningThread: Thread = new Thread(new Listener(listenTo, eventHandler, watchService, continueListening = true))
    listeningThread.start()
  }

  def eventHandler(listeningTo: Path, watchKey: WatchKey) = {
    watchKey.pollEvents().asScala.foreach { event =>
      val context = listeningTo.resolve(event.context().asInstanceOf[Path])

      println(s"Event kind: '${event.kind()}', Event context: '$context', Is directory? ${Files.isDirectory(context)}")
    }
  }
}
