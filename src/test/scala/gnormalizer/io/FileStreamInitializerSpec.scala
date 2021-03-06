package gnormalizer.io

import org.specs2.mutable.Specification

/**
  * Test for @see [[FileDataSourceHandler]]
  */
class FileStreamInitializerSpec extends Specification {

  "initializeFileStream() method" should {
    val oneElementEdgeFilePath = "src/test/resources/edge_list/test_edge_list_one_element"
    s"Works with files with a single element on it: '$oneElementEdgeFilePath'" in {
      val rawFileContents: Vector[String] = getPersistedFileContent(oneElementEdgeFilePath)
      val expectedEdge: Seq[String] = Vector("0 1")
      s"Streams the expected file content: '$expectedEdge'" in {
        rawFileContents must beEqualTo(expectedEdge)
      }
    }

    val simpleEdgeFilePath = "src/test/resources/edge_list/test_simple_edge_list"
    s"Works with files with break lines: '$simpleEdgeFilePath'" in {
      val rawFileContents: Vector[String] = getPersistedFileContent(simpleEdgeFilePath)
      val expectedFileContent: Seq[String] = Vector("0 1", "1 2", "1 3", "1 9")
      s"Streams the expected file content: '${expectedFileContent.toString}'" in {
        rawFileContents must beEqualTo(expectedFileContent)
      }
    }
  }

  private[this] def getPersistedFileContent(filePath: String): Vector[String] =
    new FileDataSourceHandler()
      .init(filePath)
      .compile
      .toList
      .unsafeRunSync()
      .toVector

}
