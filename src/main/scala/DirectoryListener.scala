import java.nio.file._

import listenable.Component
import runnable.Listener

import scala.collection.mutable

object DirectoryListener {

  //One or more digits followed by one or more occurrences of: '.' followed by one or more digits.
  val ComponentVersion = "^(\\d)+(\\.\\d+)+$".r

  val DivingCourse: mutable.Stack[String] = mutable.Stack("logs", "audit")

  val TenSeconds: Long = 10000000000L

  def main(args: Array[String]) = {
    val watchService: WatchService = FileSystems.getDefault.newWatchService()

    val listenTo: Path = Paths.get("tmp")

    listenTo.register(
      watchService,
      StandardWatchEventKinds.ENTRY_CREATE,
      StandardWatchEventKinds.ENTRY_MODIFY,
      StandardWatchEventKinds.ENTRY_DELETE
    )

    new Component(listenTo, watchService)

    val listeningThread: Thread = new Thread(new Listener(new Component(listenTo, watchService), watchService, continueListening = true), "listen-to-tmpDir")
    listeningThread.start()
  }

  //  def componentHandler(listeningTo: Path, watchKey: WatchKey, watchService: WatchService): Unit = {
  //
  //    val watchedPath: Path = watchKey.watchable().asInstanceOf[Path]
  //    println(s"Watching: ${watchedPath.toAbsolutePath}")
  //    watchKey.cancel()
  //
  //    watchKey.pollEvents().asScala.foreach { event =>
  //      val context = listeningTo.resolve(event.context().asInstanceOf[Path])
  //
  //      val kind = event.kind()
  //
  //      kind match {
  //        case StandardWatchEventKinds.ENTRY_CREATE =>
  //          if (Files.isDirectory(context) && isValidDirectoryName(context.getFileName.toString)) {
  //            println("A directory with a valid name was created")
  //
  //            val jacquesCousteau: Diver = new Diver(context, DivingCourse.clone(), System.nanoTime(), TenSeconds)
  //            val singleThreadExecutor: ExecutorService = Executors.newSingleThreadExecutor()
  //            val bottomReached: Future[Option[Path]] = singleThreadExecutor.submit(jacquesCousteau)
  //            val maybeBottom: Option[Path] = bottomReached.get()
  //
  //            if (maybeBottom.isDefined) {
  //
  //              //Find current version - and stop listening to it
  //
  //              val bottom: Path = maybeBottom.get
  //              val bottomWatchService: WatchService = FileSystems.getDefault.newWatchService()
  //              bottom.register(
  //                bottomWatchService,
  //                StandardWatchEventKinds.ENTRY_CREATE,
  //                StandardWatchEventKinds.ENTRY_MODIFY,
  //                StandardWatchEventKinds.ENTRY_DELETE
  //              )
  //              val bottomThread: Thread = new Thread(new Listener(bottom, logHandler, bottomWatchService, continueListening = true), "listen-to-the-deep")
  //              bottomThread.start()
  //            }
  //          }
  //        case StandardWatchEventKinds.ENTRY_MODIFY => println("component modify")
  //        case _ => println("component delete")
  //      }
  //    }
  //  }
  //
  //  def logHandler(listeningTo: Path, watchKey: WatchKey, watchService: WatchService): Unit = {
  //    watchKey.pollEvents().asScala.foreach { event =>
  //      val context = listeningTo.resolve(event.context().asInstanceOf[Path])
  //
  //      val kind = event.kind()
  //
  //      kind match {
  //        case StandardWatchEventKinds.ENTRY_CREATE => println(s"log handler create $context")
  //        case StandardWatchEventKinds.ENTRY_MODIFY => println(s"log handler modify $context")
  //        case _ => println(s"log handler delete $context")
  //      }
  //    }
  //  }
}
