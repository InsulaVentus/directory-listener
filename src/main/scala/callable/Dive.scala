package callable

import java.nio.file._
import java.util.concurrent.Callable

import scala.annotation.tailrec
import scala.collection.mutable
import scala.util.{Success, Try}


class Dive(divingCourse: mutable.Stack[String], runFor: Long) extends Callable[Try[WatchKey]] {
  override def call(): Try[WatchKey] = {
    Success(null)
    //    val auditLogPath: Path = directory.resolve(Paths.get("logs/audit"))
    //    if (Files.exists(auditLogPath) && Files.isDirectory(auditLogPath)) {
    //
    //      watchService.close()
    //
    ////      return auditLogPath.register()
    //    }
    //
    //    val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    //    println(s"${Thread.currentThread().getName} - Starting dive - '$runFor'")
    //    executorService.submit(new Diver(watchService, System.nanoTime(), runFor)).get()
  }

  //TODO: Simplify time assignment.
  //  def runUntil(runTime: Long, timeUnit: TimeUnit): Long = {
  //    TimeUnit.S
  //  }
}

class Diver(surface: Path, divingCourse: mutable.Stack[String], startTime: Long, runTime: Long) extends Callable[Option[Path]] {
  override def call(): Option[Path] = {
    doDive(surface, divingCourse, startTime, runTime)
  }

  @tailrec
  private def doDive(from: Path, divingCourse: mutable.Stack[String], startTime: Long, runTime: Long): Option[Path] = {

    var currentLevel: Path = from
    println(s"$currentLevel")

    if (divingCourse.isEmpty) {
      return Some(currentLevel)
    }

    if (System.nanoTime() >= (startTime + runTime)) {
      return None
    }

    //Check next level
    val nextLevel: Path = currentLevel.resolve(divingCourse.top)

    //If level exists
    if (Files.exists(nextLevel) && Files.isDirectory(nextLevel)) {
      currentLevel = nextLevel
      divingCourse.pop()
    }

    Thread.sleep(500)
    doDive(currentLevel, divingCourse, startTime, runTime)
  }
}
