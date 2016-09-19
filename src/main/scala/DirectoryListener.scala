import java.nio.file._

import runnable.Listener

import scala.collection.JavaConverters._

object DirectoryListener {

  //One or more digits followed by one or more occurrences of: '.' followed by one or more digits.
  val ComponentVersion = "^(\\d)+(\\.\\d+)+$".r

  def main(args: Array[String]) = {
    val watchService: WatchService = FileSystems.getDefault.newWatchService()

    val listenTo: Path = Paths.get("tmpDir")

    listenTo.register(
      watchService,
      StandardWatchEventKinds.ENTRY_CREATE,
      StandardWatchEventKinds.ENTRY_MODIFY,
      StandardWatchEventKinds.ENTRY_DELETE
    )

    val listeningThread: Thread = new Thread(new Listener(listenTo, componentHandler, watchService, continueListening = true), "listen-to-tmpDir")
    listeningThread.start()
  }

  def componentHandler(listeningTo: Path, watchKey: WatchKey, watchService: WatchService): Unit = {
    watchKey.pollEvents().asScala.foreach { event =>
      val context = listeningTo.resolve(event.context().asInstanceOf[Path])

      val kind = event.kind()

      kind match {
        case StandardWatchEventKinds.ENTRY_CREATE =>
          if (Files.isDirectory(context) && isValidDirectoryName(context.getFileName.toString)) {
            println("A directory with a valid name was created")

            val logWatchService: WatchService = FileSystems.getDefault.newWatchService()
            val auditDirectory: Path = context.resolve("logs/audit")
            auditDirectory.register(
              logWatchService,
              StandardWatchEventKinds.ENTRY_CREATE,
              StandardWatchEventKinds.ENTRY_MODIFY,
              StandardWatchEventKinds.ENTRY_DELETE
            )

            val listeningThread: Thread = new Thread(new Listener(auditDirectory, logHandler, logWatchService, continueListening = true))
            listeningThread.start()
          }
        case StandardWatchEventKinds.ENTRY_MODIFY => println("component modify")
        case _ => println("component delete")
      }
    }
  }

  def logHandler(listeningTo: Path, watchKey: WatchKey, watchService: WatchService): Unit = {
    watchKey.pollEvents().asScala.foreach { event =>
      val context = listeningTo.resolve(event.context().asInstanceOf[Path])

      val kind = event.kind()

      kind match {
        case StandardWatchEventKinds.ENTRY_CREATE => println(s"log handler create $context")
        case StandardWatchEventKinds.ENTRY_MODIFY => println(s"log handler modify $context")
        case _ => println(s"log handler delete $context")
      }
    }
  }

  def isValidDirectoryName(directory: String): Boolean = {
    ComponentVersion.pattern.matcher(directory).matches()
  }
}
