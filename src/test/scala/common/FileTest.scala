package common

import java.nio.file.Path

import org.apache.commons.io.FileUtils

import scala.util.Try

object FileTest {
  def removeRecursive(directory: Path) = {
    Try(FileUtils.deleteDirectory(directory.toFile))
  }
}
