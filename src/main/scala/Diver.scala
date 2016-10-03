import java.nio.file._
import java.util.concurrent.Callable

import scala.annotation.tailrec
import scala.collection.mutable

class Diver(surface: Path, divingCourse: mutable.Stack[String], startTime: Long, runTime: Long) extends Callable[Option[Path]] {
  override def call(): Option[Path] = {
    doDive(surface, divingCourse, startTime, runTime)
  }

  @tailrec
  private def doDive(from: Path, divingCourse: mutable.Stack[String], startTime: Long, runTime: Long): Option[Path] = {
    var currentLevel: Path = from
    println(s"$currentLevel")

    if (divingCourse.isEmpty) {
      Some(currentLevel)
    } else if (System.nanoTime() >= (startTime + runTime)) {
      None
    } else {
      val nextLevel: Path = currentLevel.resolve(divingCourse.top)

      if (Files.exists(nextLevel) && Files.isDirectory(nextLevel)) {
        currentLevel = nextLevel
        divingCourse.pop()
      }
      Thread.sleep(500)
      doDive(currentLevel, divingCourse, startTime, runTime)
    }
  }
}
