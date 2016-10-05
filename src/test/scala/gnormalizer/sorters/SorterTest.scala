package gnormalizer.sorters

import gnormalizer.models.Edge
import org.specs2.mutable.Specification
import org.specs2.specification.core.Fragments

import scala.util.Random

abstract class SorterTest extends Specification {

  val numParallelVertex : Int

  def generateSorter(bucketSize : Int): Sorter

  private[this] val testBucketSize: Int = 50

  private[this] def generateTestEdges(numberEdges: Int, graphDegree: Int): Seq[Edge] = {
    Random.shuffle(
      (0 until numberEdges).
        map(i => (0 until graphDegree).map(degree => Edge(i, degree))).
        flatten
    )
  }

  private[this] val testGraphDegrees: Seq[Int] = Seq(1, 25)

  testGraphDegrees.foldLeft(Fragments()) {
    (fragments, degree) => {
      fragments.append(
        s"Inserts and sort graphs with degree: '$degree'" should {
          "Test if the sorting is done as expected when using a single bucket" should {
            val numberTestEdges: Int = testBucketSize
            val bucketSize: Int = degree * testBucketSize
            val sorter = generateSorter(testBucketSize)
            val testEdges: Seq[Edge] = generateTestEdges(numberTestEdges, degree)
            // Inserts the edges sequentially
            testEdges.foreach(sorter.addEdgeToResult)
            "Use only one bucket was used for doing the sorting" in {
              sorter.countNumberBuckets() must beEqualTo(1)
            }
            s"The result must have ${numberTestEdges * degree} elements" in {
              sorter.resultStream().size must beEqualTo(numberTestEdges * degree)
            }
            s"Sorts the inserted $numberTestEdges edges as expected" in {
              testEdges.sortWith(_.compareTo(_) < 0) must
                beEqualTo(sorter.resultStream().toIndexedSeq)
            }
          }
          "Test if the sorting is done as expected when using multiple buckets" should {
            val numVertex: Int = testBucketSize * 10
            val sorter = generateSorter(testBucketSize)
            val testEdges: Seq[Edge] = generateTestEdges(numVertex, degree)
            // Expectations
            val expectedNumberOfBuckets: Int = numVertex / testBucketSize
            // Inserts the edges sequentially
            testEdges.foreach(sorter.addEdgeToResult)
            s"$expectedNumberOfBuckets buckets were used for doing the sorting" in {
              sorter.countNumberBuckets() must beEqualTo(expectedNumberOfBuckets)
            }
            s"The result must have ${numVertex * degree} elements" in {
              testEdges.size must beEqualTo(numVertex * degree)
            }
            s"Sorts the inserted $numVertex edges as expected" in {
              testEdges.sortWith(_.compareTo(_) < 0) mustEqual sorter.resultStream().toIndexedSeq
            }
          }
          s"Test if $numParallelVertex Vertex adjacency's can be done in parallel" should {
            val sorter = generateSorter(testBucketSize)
            val testEdges: Seq[Edge] = generateTestEdges(numParallelVertex, degree)
            // Expectations
            val expectedNumberOfBuckets: Int = numParallelVertex / testBucketSize
            // Inserts the edges in parallel
            testEdges.par.foreach(sorter.addEdgeToResult)
            // Tests:
            s"$expectedNumberOfBuckets buckets were used for doing the sorting" in {
              sorter.countNumberBuckets() must beEqualTo(expectedNumberOfBuckets)
            }
            s"The result must have ${numParallelVertex * degree} elements" in {
              sorter.resultStream().size must beEqualTo(numParallelVertex * degree)
            }
            s"Sorts the inserted ${numParallelVertex * degree} edges as expected" in {
              testEdges.sortWith(_.compareTo(_) < 0) mustEqual sorter.resultStream().toIndexedSeq
            }
          }
        }
      )
    }
  }
}
