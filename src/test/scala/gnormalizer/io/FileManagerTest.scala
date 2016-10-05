package gnormalizer.io

import java.io.File
import java.util.UUID

import org.specs2.mutable.Specification

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

/**
  * Test for @see [[FileManager]]
  */
class FileManagerTest extends Specification {

  implicit val ec: ExecutionContext = ExecutionContext.Implicits.global

  private[this] val timeout: Duration = Duration.Inf

  sequential

  "The EdgeWritter helper methods" should {
    sequential

    val testFolder = s"target/test${UUID.randomUUID()}"
    val temporalFileFolder = s"$testFolder/temp/"

    s"makeDir() method -> Creates the temporal folder with name: '$temporalFileFolder'" >> {
      FileManager.makeDir(temporalFileFolder)
      new File(temporalFileFolder).exists() must beTrue
    }

    // The following test will check also if the method removes the inner folders recursively.
    s"removeDirAsync -> Must remove the folder '$testFolder' successfully" >> {
      Await.result(
        awaitable = FileManager.removeDirAsync(temporalFileFolder),
        atMost = timeout
      )
      new File(temporalFileFolder).exists() must beFalse
    }
  }
}
