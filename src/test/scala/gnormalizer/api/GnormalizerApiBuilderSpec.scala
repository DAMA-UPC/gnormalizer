package gnormalizer
package api

import babel.graph.Edge
import cats.effect.IO
import gnormalizer.io.FileDataSourceHandler
import gnormalizer.normalizers.GraphNormalizer
import org.specs2.mutable.Specification
import org.scalamock.specs2.MockContext

/**
  * Specification tests for [[GnormalizerApiBuilder]].
  */
class GnormalizerApiBuilderSpec extends Specification {

  private[this] val testInputFilePath = "inputFilePath"
  private[this] val testInputFileFormat = EdgeList
  private[this] val testOutputFilePath = "target/temp/outputFilePath"
  private[this] val testOutputFileFormat = EdgeList
  private[this] val testBucketSize = 5111
  private[this] val testInitDeserializationAtLine = 4L

  "GnormalizerApiBuilder" in new MockContext {

      val mockGraphNormalizer: GraphNormalizer = mock[GraphNormalizer]

      val apiBuilder = new GnormalizerApiBuilder(mockGraphNormalizer)

      val testEdge1 = Edge(1, 2)
      val testEdge2 = Edge(6, 2)

      (mockGraphNormalizer.obfuscateEdgeListGraph _)
        .expects(testInputFilePath, Some(testInitDeserializationAtLine))
        .returning(IO.pure(Stream(testEdge1, testEdge2)))

      apiBuilder
        .inputFile(testInputFilePath, testInputFileFormat)
        .outputFile(testOutputFilePath, testOutputFileFormat)
        .execute(bucketSize = Some(testBucketSize),
               startDeserializationAtLine = Some(testInitDeserializationAtLine))

      getPersistedFileContent(testOutputFilePath) must beEqualTo(
      Vector(testEdge1.toString, testEdge2.toString)
    )
  }

  private[this] def getPersistedFileContent(filePath: String): Vector[String] =
    new FileDataSourceHandler()
      .init(filePath)
      .compile
      .toList
      .unsafeRunSync()
      .toVector
      .filter(_.nonEmpty)
}
