import java.nio.file.{Path, Paths}

object Main extends App {

  val listenToThis1 = "/Users/insulaventus/data/tmpDirs/listen-to-this-1"
  val listenToThis2 = "/Users/insulaventus/data/tmpDirs/listen-to-this-2"

  val components: Vector[Path] = Vector(Paths.get(listenToThis1), Paths.get(listenToThis2))
    .flatMap { component =>
      SecondDiver.getDirectoriesDirectlyUnder(component) :+ component
    }

  println(components)
}
