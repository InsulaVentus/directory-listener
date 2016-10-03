import java.nio.file.{Files, Path}

import scala.annotation.tailrec
import scala.collection.JavaConverters._
import scala.collection.mutable

object SecondDiver {


  def testTest(): Vector[String] = {
    Vector("first", "second", "third")
      .foldLeft(Vector.empty[String]) { (strings, aString) =>
        getAnother match {
          case Some(s) => strings :+ aString :+ s
          case None => strings :+ aString
        }
      }
  }

  def getAnother: Option[String] = {
    Some("Another")
  }

  def getDirectoriesDirectlyUnder(component: Path): Vector[Path] = {
    Files.newDirectoryStream(component)
      .asScala
      .filter(Files.exists(_))
      .filter(Files.isDirectory(_))
      .foldLeft(Vector.empty[Path]) { (directories, version) =>
        doDive(version, mutable.Stack("logs", "audit")) match {
          case Some(logDir) => directories :+ logDir
          case None => directories
        }
      }
  }

  @tailrec
  private def doDive(from: Path, divingCourse: mutable.Stack[String]): Option[Path] = {
    val currentLevel: Path = from
    println(s"$currentLevel")

    if (Files.notExists(currentLevel) || !Files.isDirectory(currentLevel)) {
      None
    } else if (divingCourse.isEmpty) {
      Some(currentLevel)
    } else {
      doDive(currentLevel.resolve(divingCourse.pop), divingCourse)
    }
  }
}
