import java.nio.file.WatchEvent.Kind
import java.nio.file.{StandardWatchEventKinds, WatchKey}

trait Listenable {

  val CreateModifyDelete: Array[Kind[_]] = Array(
    StandardWatchEventKinds.ENTRY_CREATE,
    StandardWatchEventKinds.ENTRY_MODIFY,
    StandardWatchEventKinds.ENTRY_DELETE
  )

  def isComponent(watchKey: WatchKey): Boolean

  /**
    * Callback function for a file event.
    */
  def handleEvent(watchKey: WatchKey): Unit

  def register(): Listenable
}
