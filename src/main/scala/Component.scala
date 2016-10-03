import java.nio.file._
import java.util.concurrent.TimeUnit.SECONDS
import java.util.concurrent.{ExecutorService, Executors, Future, TimeUnit}

import Component.{asNanoSeconds, isValidDirectoryName}

import scala.collection.JavaConverters._
import scala.collection.mutable

/**
  * A component is the parent directory of an application.
  * E.g. '/opt/apps/dbf/application-name/' (for the ndf environments).
  *
  * The first descendants to a component should be directories containing the different application versions.
  * E.g. '/opt/apps/dbf/application-name/1.1', '/opt/apps/dbf/application-name/1.2/' etc
  *
  * Each version should contain a log directory.
  * E.g. /opt/apps/dbf/application-name/1.1/logs/audit, /opt/apps/dbf/application-name/1.2/logs/audit etc
  */
case class Component(directory: Path, queue: WatchService, var log: Log) extends Listenable {

  val DivingCourse: mutable.Stack[String] = mutable.Stack("logs", "audit")

  val DiveTimer: Long = asNanoSeconds(60, SECONDS)

  override def handleEvent(watchKey: WatchKey): Unit = {
    val events = watchKey.pollEvents().asScala
    watchKey.reset()

    events.foreach { event =>
      val context = directory.resolve(event.context().asInstanceOf[Path])
      event.kind() match {
        case StandardWatchEventKinds.ENTRY_CREATE =>
          println(s"Create event for: ${this.toString}")
          findLog(context, DivingCourse.clone(), DiveTimer) match {
            case Some(replacementLog) =>
              log = Log(replacementLog, queue)
              log.register()

            case _ =>
          }
        case eventKind => println(s"${eventKind.name()} event for: ${this.toString}")
      }
    }
  }

  /**
    * Determine if this component matches a given [[java.nio.file.WatchKey]]
    */
  override def isComponent(watchKey: WatchKey): Boolean = {
    watchKey.watchable().asInstanceOf[Path].equals(directory)
  }

  /**
    * Determine if this components log matches a given [[java.nio.file.WatchKey]]
    */
  def isLog(watchKey: WatchKey): Boolean = {
    log.isComponent(watchKey)
  }

  /**
    * Get the matching a given [[java.nio.file.WatchKey]]
    *
    * @return this component or its log if the [[java.nio.file.WatchKey]] matches one of these, None otherwise
    */
  def getListenable(watchKey: WatchKey): Option[Listenable] = {
    if (isComponent(watchKey)) {
      Some(this)
    } else if (isLog(watchKey)) {
      Some(log)
    } else {
      None
    }
  }

  /**
    * Determine, by using a , whether a given directory has a certain set of
    * subdirectories. The directories need not be present when calling this method, but must appear within a given time.
    *
    * @param directory    the base directory from which we look for sub directories
    * @param divingCourse a stack of sub directories. The starts from stack.top
    * @param timer        in nanoseconds. If the sub directories has not been found within this time, None is returned.
    * @return Some(Path) if we reach the bottom of the stack, None otherwise.
    */
  def findLog(directory: Path, divingCourse: mutable.Stack[String], timer: Long): Option[Path] = {
    if (Files.isDirectory(directory) && isValidDirectoryName(directory.getFileName.toString)) {
      println(s"A directory with a valid name was created: ${directory.toAbsolutePath.toString}")

      val diver: Diver = new Diver(directory, divingCourse, System.nanoTime(), timer)
      val singleThreadExecutor: ExecutorService = Executors.newSingleThreadExecutor()
      val bottomReached: Future[Option[Path]] = singleThreadExecutor.submit(diver)
      bottomReached.get()
    } else {
      None
    }
  }

  /**
    * Register this component and its Log with its queue ([[java.nio.file.WatchService]]). After registering, file events for the
    * component will be pushed to the queue.
    */
  override def register(): Component = {
    println(s"Registering component - ${directory.toAbsolutePath.toString}")
    directory.register(queue, CreateModifyDelete)
    log.register()
    this
  }

  override def toString = s"Component(${directory.toAbsolutePath.toString}) with $log"
}

object Component {

  // One or more digits followed by one or more occurrences of: '.' followed by one or more digits.
  val ComponentVersion = "^(\\d)+(\\.\\d+)+$".r

  def isValidDirectoryName(directory: String): Boolean = {
    ComponentVersion.pattern.matcher(directory).matches()
  }

  def asNanoSeconds(seconds: Long, timeUnit: TimeUnit): Long = {
    TimeUnit.NANOSECONDS.convert(seconds, timeUnit)
  }
}
