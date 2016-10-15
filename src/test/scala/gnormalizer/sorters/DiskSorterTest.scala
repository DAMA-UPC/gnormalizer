package gnormalizer.sorters

import better.files._
import gnormalizer.models.Edge

/**
  * Test for @see [[DiskSorter]]
  *
  * Inherits all the test from @see [[SorterTest]]
  */
class DiskSorterTest extends SorterTest {

  /**
    * @inheritdoc
    */
  override def generateSorter(bucketSize: Int): Sorter = {
    DiskSorter(maxVerticesPerBucket = bucketSize)
  }

  "appendEdgeToFile() method" in {
    val edges: Seq[Edge] = (0 to 1).map(i => Edge(i, i))

    "Buffers the edge content, if not exceeding the maximum buffer edge size" in {
      val sorter = DiskSorter()
      edges.foreach(sorter.addEdgeToResult)
      sorter.usedFilePaths() must beEmpty
    }

    "Stores in disk the inserted edges, if the maximum edge size is exceeded" in {
      val multipleBucketsSorter = DiskSorter(maxVerticesPerBucket = 1, maxEdgesPerBucket = 0)
      edges.foreach(multipleBucketsSorter.addEdgeToResult)
      // Assert 1: There must be two generated files.
      (multipleBucketsSorter.usedFilePaths().size must beEqualTo(edges.size)) &&
        // Assert 2: The generated file must contain both inserted edges, in any order.
        (multipleBucketsSorter.usedFilePaths().map(_.toFile.lines).reduce(_ ++ _).toSeq
          must containAllOf(edges.map(_.toString)))
    }
  }
}
