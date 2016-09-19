package callable

import java.util.concurrent.{ExecutorService, Executors, TimeUnit}

class Dive(timeout: Long, timeUnit: TimeUnit) extends Runnable {
  override def run() = {
    val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    println(s"${Thread.currentThread().getName} - Starting dive - '$timeout' - '$timeUnit'")
    executorService.submit(new Diver).get(timeout, timeUnit)
    println(s"${Thread.currentThread().getName} - Dive finished")
  }
}

class Diver extends Runnable {

  override def run() = {
    while (!Thread.currentThread().isInterrupted) {
      println(s"${Thread.currentThread().getName} - Diving")
      Thread.sleep(5000)
    }
  }

  //  override def call(): String = {
  //    while (!Thread.interrupted()) {
  //      Thread.sleep(5000)
  //    }
  //    "Done"
  //  }

}

