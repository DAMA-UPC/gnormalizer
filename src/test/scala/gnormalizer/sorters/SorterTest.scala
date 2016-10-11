package gnormalizer.sorters

import gnormalizer.models.Edge
import org.specs2.mutable.Specification
import org.specs2.specification.core.Fragments

import scala.util.Random

/**
  * Base test for all sorters inheriting @see [[Sorter]].
  */
abstract class SorterTest extends Specification {

  /**
    * Generates a [[Sorter]] that will be used for the [[SorterTest]] tests.
    */
  def generateSorter(maxVerticesPerBucket: Int): Sorter

  /**
    * Number of [[gnormalizer.models.Vertex]] used for the parallel tests.
    */
  val numParallelVertices: Int

  private[this] val testBucketSize: Int = 50

  private[this] def generateTestEdges(numberEdges: Int, graphDegree: Int): Seq[Edge] = {
    Random.shuffle(
      (0 until numberEdges).
        map(i => (0 until graphDegree).map(degree => Edge(i, degree))).
        flatten
    )
  }

  @inline private[this] val graphDegreeToTest1: Int = 1
  @inline private[this] val graphDegreeToTest2: Int = 25

  Seq(graphDegreeToTest1, graphDegreeToTest2).foldLeft(Fragments()) {
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
            s"The result must have ${numberTestEdges * degree} elements in one bucket" in {
              sorter.resultStream().size must beEqualTo(numberTestEdges * degree)
            }
            s"Sorts the inserted $numberTestEdges edges as expected" in {
              testEdges.sortWith(_.compareTo(_) < 0) must
                beEqualTo(sorter.resultStream().toList)
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
              sorter.countNumberEdges() must beEqualTo(numVertex * degree)
            }
            s"Sorts the inserted $numVertex edges as expected using multiple buckets" in {
              testEdges.sortWith(_.compareTo(_) < 0) mustEqual sorter.resultStream().toIndexedSeq
            }
          }
          s"Test if $numParallelVertices Vertex adjacency's can be done in parallel" should {
            val sorter = generateSorter(Sorter.defaultMaxVerticesPerBucket)
            val testEdges: Seq[Edge] = generateTestEdges(numParallelVertices, degree)
            // Expectations
            val expectedNumberOfBuckets = numParallelVertices / Sorter.defaultMaxVerticesPerBucket
            // Inserts the edges in parallel
            testEdges.par.foreach(sorter.addEdgeToResult)
            // Tests:
            s"$expectedNumberOfBuckets buckets were used for doing the sorting in parallel" in {
              sorter.countNumberBuckets() must beEqualTo(expectedNumberOfBuckets)
            }
            s"The result must have ${numParallelVertices * degree} parallel inserted elements" in {
              sorter.resultStream().size must beEqualTo(numParallelVertices * degree)
            }
            s"Sorts the ${numParallelVertices * degree} parallel inserted edges as expected" in {
              sorter.resultStream().toIndexedSeq must beEqualTo(
                testEdges.sortWith(_.compareTo(_) < 0)
              )
            }
          }
        }
      )
    }
  }
}
