package gnormalizer.sorters

import babel.graph.Edge
import better.files._

/**
  * Test for @see [[DiskSorter]]
  *
  * Inherits all the test from @see [[SorterSpec]]
  */
class DiskSorterSpec extends SorterSpec {

  override def generateSorter(maxVerticesPerBucket: Int): Sorter = {
    new DiskSorter(maxVerticesPerBucket = maxVerticesPerBucket)
  }

  override def defaultNumberVertexesPerBucket: Int = DiskSorter.defaultMaxVertexesPerBucket

  "appendEdgeToFile() method" in {
    val edges: Seq[Edge] = (0 to 1).map(i => Edge(i, i))

    "Buffers the edge content, if not exceeding the maximum buffer edge size" in {
      val sorter = new DiskSorter()
      edges.foreach(sorter.addEdgeToResult)
      sorter.usedFilePaths() must beEmpty
    }

    "Stores in disk the inserted edges, if the maximum edge size is exceeded" in {
      val multipleBucketsSorter = new DiskSorter(maxVerticesPerBucket = 1, maxEdgesPerBucket = 0)
      edges.foreach(multipleBucketsSorter.addEdgeToResult)
      // Assert 1: There must be two generated files.
      (multipleBucketsSorter.usedFilePaths().size must beEqualTo(edges.size)) &&
      // Assert 2: The generated file must contain both inserted edges, in any order.
      (multipleBucketsSorter.usedFilePaths().map(_.toFile.lines).foldLeft(Seq[String]())(_ ++ _)
        must containAllOf(edges.map(_.toString)))
    }
  }
}
