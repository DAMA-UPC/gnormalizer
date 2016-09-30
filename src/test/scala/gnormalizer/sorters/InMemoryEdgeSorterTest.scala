package gnormalizer.sorters

import gnormalizer.models.Edge
import org.specs2.mutable.Specification

import scala.util.Random

class InMemoryEdgeSorterTest extends Specification {

  private[this] val testBucketSize: Int = 5

  private[this] def generateTestEdges(numberEdges: Int): Seq[Edge] =
    Random.shuffle(
      (0 until numberEdges).
        map(i => Edge(i, i))
    )

  "Test if the sorting is done as expected when using a single bucket" should {
    val sorter = InMemoryEdgeSorter(resultTreeNodeBucketKeySize = testBucketSize)
    val fillBucketSizeTestEdges: Int = testBucketSize / 2
    val testEdges: Seq[Edge] = generateTestEdges(fillBucketSizeTestEdges)
    // Inserts the edges sequentially
    testEdges.foreach(sorter.addEdgeToResult)
    "Use only one bucket was used for doing the sorting" in {
      sorter.numberBuckets must beEqualTo(1)
    }
    s"The result must have $fillBucketSizeTestEdges elements" in {
      sorter.resultStream().size must beEqualTo(fillBucketSizeTestEdges)
    }
    s"Sorts the inserted $fillBucketSizeTestEdges edges as expected" in {
      testEdges.sortWith(_.compareTo(_) < 0) must beEqualTo(sorter.resultStream().toIndexedSeq)
    }
  }

  "Test if the sorting is done as expected when using multiple buckets" should {
    val overflowedBucketTestEdges: Int = testBucketSize * 10
    val sorter = InMemoryEdgeSorter(resultTreeNodeBucketKeySize = testBucketSize)
    val testEdges: Seq[Edge] = generateTestEdges(overflowedBucketTestEdges)
    // Expectations
    val expectedNumberOfBuckets: Int = overflowedBucketTestEdges / testBucketSize
    // Inserts the edges sequentially
    testEdges.foreach(sorter.addEdgeToResult)
    s"$expectedNumberOfBuckets buckets were used for doing the sorting" in {
      sorter.numberBuckets must beEqualTo(expectedNumberOfBuckets)
    }
    s"The result must have $overflowedBucketTestEdges elements" in {
      testEdges.size must beEqualTo(overflowedBucketTestEdges)
    }
    s"Sorts the inserted $overflowedBucketTestEdges edges as expected" in {
      testEdges.sortWith(_.compareTo(_) < 0) mustEqual sorter.resultStream().toIndexedSeq
    }
  }

  "Test if the Edge insertions can be done in parallel" should {
    val numberParallelBruteForceTestEdges: Int = 10000
    val sorter = InMemoryEdgeSorter(resultTreeNodeBucketKeySize = testBucketSize)
    val testEdges: Seq[Edge] = generateTestEdges(numberParallelBruteForceTestEdges)
    // Expectations
    val expectedNumberOfBuckets: Int = numberParallelBruteForceTestEdges / testBucketSize
    // Inserts the edges in parallel
    testEdges.par.foreach(sorter.addEdgeToResult)
    // Tests:
    s"$expectedNumberOfBuckets buckets were used for doing the sorting" in {
      sorter.numberBuckets must beEqualTo(expectedNumberOfBuckets)
    }
    s"The result must have $numberParallelBruteForceTestEdges elements" in {
      sorter.resultStream().size must beEqualTo(numberParallelBruteForceTestEdges)
    }
    s"Sorts the inserted $numberParallelBruteForceTestEdges edges as expected" in {
      testEdges.sortWith(_.compareTo(_) < 0) mustEqual sorter.resultStream().toIndexedSeq
    }
  }
}
