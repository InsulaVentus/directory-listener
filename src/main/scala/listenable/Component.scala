package listenable

import java.nio.file._
import java.util.concurrent.{ExecutorService, Executors, Future}

import callable.Diver
import listenable.Component.isValidDirectoryName
import runnable.Listener

import scala.collection.JavaConverters._
import scala.collection.mutable

/**
  * A 'watched' directory.
  */
class Component(directory: Path, watchService: WatchService) extends Listenable {

  val DivingCourse: mutable.Stack[String] = mutable.Stack("logs", "audit")

  val TenSeconds: Long = 10000000000L

  override def getName: String = {
    directory.getFileName.toString
  }

  override def notify(watchKey: WatchKey): Unit = {
    //    val watchedPath: Path = watchKey.watchable().asInstanceOf[Path]
    //    println(s"Watching: ${watchedPath.toAbsolutePath}")
    //    watchKey.cancel()

    watchKey.pollEvents().asScala.foreach { event =>
      val context = directory.resolve(event.context().asInstanceOf[Path])

      val kind = event.kind()

      kind match {
        case StandardWatchEventKinds.ENTRY_CREATE =>
          if (Files.isDirectory(context) && isValidDirectoryName(context.getFileName.toString)) {
            println("A directory with a valid name was created")

            val jacquesCousteau: Diver = new Diver(context, DivingCourse.clone(), System.nanoTime(), TenSeconds)
            val singleThreadExecutor: ExecutorService = Executors.newSingleThreadExecutor()
            val bottomReached: Future[Option[Path]] = singleThreadExecutor.submit(jacquesCousteau)
            val maybeBottom: Option[Path] = bottomReached.get()

            if (maybeBottom.isDefined) {

              //Find current version - and stop listening to it

              val bottom: Path = maybeBottom.get
              val bottomWatchService: WatchService = FileSystems.getDefault.newWatchService()
              bottom.register(
                bottomWatchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE
              )
              val bottomThread: Thread = new Thread(new Listener(new Log(bottom, bottomWatchService), bottomWatchService, continueListening = true), "listen-to-the-deep")
              bottomThread.start()
            }
          }
        case StandardWatchEventKinds.ENTRY_MODIFY => println("component modify")
        case _ => println("component delete")
      }
    }
  }
}

object Component {

  //One or more digits followed by one or more occurrences of: '.' followed by one or more digits.
  val ComponentVersion = "^(\\d)+(\\.\\d+)+$".r

  def isValidDirectoryName(directory: String): Boolean = {
    ComponentVersion.pattern.matcher(directory).matches()
  }
}
