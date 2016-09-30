package gnormalizer.io

import org.specs2.mutable.Specification

/**
  * Test for @see [[FileStreamInitializer]]
  */
class FileStreamInitializerTest extends Specification {

  "initializeFileStream() method" should {
    val oneElementEdgeFilePath = "src/test/resources/edge_list/test_edge_list_one_element"
    s"Works with files with no break lines: '$oneElementEdgeFilePath'" in {
      val rawFileContents: Vector[String] = {
        FileStreamInitializer.
          initializeFileStream(oneElementEdgeFilePath).
          runLog.
          unsafeRun()
      }
      s"Contains only a streamed value (No split is done yet)" in {
        rawFileContents must haveSize(1)
      }
      val expectedEdge: String = "0 1"
      s"Streams the expected file content: '$expectedEdge'" in {
        rawFileContents must contain(expectedEdge)
      }
    }

    val simpleEdgeFilePath = "src/test/resources/edge_list/test_simple_edge_list"
    s"Works with files with break lines: '$simpleEdgeFilePath'" in {
      val rawFileContents: Vector[String] = {
        FileStreamInitializer.
          initializeFileStream(simpleEdgeFilePath).
          runLog.
          unsafeRun()
      }
      s"Contains only a streamed value (No split is done yet)" in {
        rawFileContents must haveSize(1)
      }
      val expectedFileContent: String = "0 1\n1 2\n1 3\n1 9"
      s"Streams the expected file content: '0 1\\1 2\\n1 3\\n1 9'" in {
        rawFileContents must contain(expectedFileContent)
      }
    }
  }
}
