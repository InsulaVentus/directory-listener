package listenable

import java.nio.file.WatchEvent.Kind
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
class Component(directory: Path, watchService: WatchService, var log: Log) extends Listenable(directory) {

  val DivingCourse: mutable.Stack[String] = mutable.Stack("logs", "audit")

  val TenSeconds: Long = 10000000000L

  override def notify(watchKey: WatchKey): Unit = {
    watchKey.pollEvents().asScala.foreach { event =>
      val context = directory.resolve(event.context().asInstanceOf[Path])

      val kind = event.kind()

      kind match {
        case StandardWatchEventKinds.ENTRY_CREATE =>
          val maybeLog: Option[Path] = findLog(context)
          if (maybeLog.isDefined) {
            log.stopListening()
            val replacementLog: Log = Log(maybeLog.get, FileSystems.getDefault.newWatchService())
            log = replacementLog
            new Thread(Listener(replacementLog, continueListening = true), "listen-to-the-deep").start()

          }
        case StandardWatchEventKinds.ENTRY_MODIFY => println("component modify")
        case _ => println("component delete")
      }
    }
  }

  def findLog(directory: Path): Option[Path] = {
    if (Files.isDirectory(directory) && isValidDirectoryName(directory.getFileName.toString)) {
      println("A directory with a valid name was created")

      val jacquesCousteau: Diver = new Diver(directory, DivingCourse.clone(), System.nanoTime(), TenSeconds)
      val singleThreadExecutor: ExecutorService = Executors.newSingleThreadExecutor()
      val bottomReached: Future[Option[Path]] = singleThreadExecutor.submit(jacquesCousteau)
      return bottomReached.get()
    }
    None
  }

  override def get(): WatchKey = watchService.take()
}

object Component {

  //One or more digits followed by one or more occurrences of: '.' followed by one or more digits.
  val ComponentVersion = "^(\\d)+(\\.\\d+)+$".r

  val CreateModifyDelete: Array[Kind[_]] = Array(
    StandardWatchEventKinds.ENTRY_CREATE,
    StandardWatchEventKinds.ENTRY_MODIFY,
    StandardWatchEventKinds.ENTRY_DELETE
  )

  def isValidDirectoryName(directory: String): Boolean = {
    ComponentVersion.pattern.matcher(directory).matches()
  }

  def apply(directory: Path, watchService: WatchService, log: Log): Component = {
    directory.register(watchService, CreateModifyDelete)
    new Component(directory, watchService, log)
  }
}
