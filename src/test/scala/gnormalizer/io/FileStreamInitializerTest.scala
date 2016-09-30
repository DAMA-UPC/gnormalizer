package gnormalizer.io

import org.specs2.mutable.Specification

/**
  * Test for @see [[FileStreamInitializer]]
  */
class FileStreamInitializerTest extends Specification {

  def initLoggedFileStream(filePath: String): Vector[String] =
    FileStreamInitializer.
      initializeFileStream(filePath).
      runLog.
      unsafeRun()

  "initializeFileStream() method" should {
    val oneElementEdgeFilePath = "src/test/resources/edge_list/test_edge_list_one_element"
    s"Works with files with no break lines: '$oneElementEdgeFilePath'" in {
      val rawFileContents: Vector[String] = initLoggedFileStream(oneElementEdgeFilePath)
      s"All content from $oneElementEdgeFilePath is extracted without any type split" in {
        rawFileContents must haveSize(1)
      }
      val expectedEdge: String = "0 1"
      s"Streams the expected file content: '$expectedEdge'" in {
        rawFileContents must contain(expectedEdge)
      }
    }

    val simpleEdgeFilePath = "src/test/resources/edge_list/test_simple_edge_list"
    s"Works with files with break lines: '$simpleEdgeFilePath'" in {
      val rawFileContents: Vector[String] = initLoggedFileStream(simpleEdgeFilePath)
      s"Extracts the content from $simpleEdgeFilePath without any type split" in {
        rawFileContents must haveSize(1)
      }
      val expectedFileContent: String = "0 1\n1 2\n1 3\n1 9"
      s"Streams the expected file content: '0 1\\1 2\\n1 3\\n1 9'" in {
        rawFileContents must contain(expectedFileContent)
      }
    }
  }
}
