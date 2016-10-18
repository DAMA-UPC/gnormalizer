package gnormalizer.sorters

import gnormalizer.models.Edge
import org.specs2.mutable.Specification
import org.specs2.specification.core.{Fragment, Fragments}

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
    * Obtains the default maximum amount of [[gnormalizer.models.Vertex#source]]
    * in a specific [[Sorter]] implementation.
    */
  def defaultNumberVertexesPerBucket: Int

  private[this] def generateTestEdges(numberEdges: Int, graphDegree: Int): Seq[Edge] = {
    Random.shuffle(
      (0 until numberEdges).
        map(
          vertex =>
            (0 until graphDegree).
              map(degree => Edge(vertex, degree))
        )
        .reduce(_ ++ _)
    ).toList
  }

  @inline
  private[this] def testSuite(sorter: Sorter,
                              unorderedEdgesSeq: Seq[Edge],
                              expectedNumberBuckets: Long): Fragment = {
    s"Check if the expected '$expectedNumberBuckets' buckets where used" in {
      sorter.countNumberBuckets() must beEqualTo(expectedNumberBuckets)
    }
    s"The result must contain ${unorderedEdgesSeq.size} elements" in {
      sorter.countNumberEdges() must beEqualTo(unorderedEdgesSeq.size)
    }
    s"The ${unorderedEdgesSeq.size} vertices must be properly sorted" in {
      sorter.resultStream().equals(
        unorderedEdgesSeq.sortWith(_.compareTo(_) < 0)
      )
    }
  }

  @inline private[this] val testBucketSize: Int = 50
  @inline private[this] val numParallelVertices: Int = 10000
  @inline private[this] val graphDegreeToTest1: Int = 1
  @inline private[this] val graphDegreeToTest2: Int = 25
  Seq(graphDegreeToTest1, graphDegreeToTest2).foldLeft(Fragments()) {
    (fragments, degree) => {
      fragments.append(
        s"Inserts and sort graphs with degree: '$degree'" should {
          "Test if the sorting is done as expected when using a single bucket" should {
            val numberTestEdges: Int = testBucketSize
            val sorter = generateSorter(testBucketSize)
            val testEdges: Seq[Edge] = generateTestEdges(numberTestEdges, degree)
            val expectedNumberOfBuckets = 1L
            // Inserts the edges sequentially
            testEdges.foreach(sorter.addEdgeToResult)
            // Tests
            testSuite(
              sorter = sorter,
              unorderedEdgesSeq = testEdges,
              expectedNumberBuckets = expectedNumberOfBuckets
            )
          }
          "Test if the sorting is done as expected when using multiple buckets" should {
            val numVertex: Int = testBucketSize * 10
            val sorter = generateSorter(testBucketSize)
            val testEdges: Seq[Edge] = generateTestEdges(numVertex, degree)
            val expectedNumberOfBuckets: Int = numVertex / testBucketSize
            // Inserts the edges sequentially
            testEdges.foreach(sorter.addEdgeToResult)
            // Tests
            testSuite(
              sorter = sorter,
              unorderedEdgesSeq = testEdges,
              expectedNumberBuckets = expectedNumberOfBuckets
            )
          }
          s"Test if $numParallelVertices Vertex adjacency's can be done in parallel" should {
            val sorter = generateSorter(defaultNumberVertexesPerBucket)
            val testEdges: Seq[Edge] = generateTestEdges(numParallelVertices, degree)
            val expectedNumberOfBuckets = numParallelVertices / defaultNumberVertexesPerBucket
            // Inserts the edges in parallel
            testEdges.par.foreach(sorter.addEdgeToResult)
            // Tests
            testSuite(
              sorter = sorter,
              unorderedEdgesSeq = testEdges,
              expectedNumberBuckets = expectedNumberOfBuckets
            )
          }
        }
      )
    }
  }
  val highVertexDegree: Int = numParallelVertices / 2
  val numberHighDegreeVertex: Int = 5
  s"With '$numberHighDegreeVertex' vertexes test a graph degree of '$highVertexDegree'" in {
    val sorter = generateSorter(defaultNumberVertexesPerBucket)
    val testEdges: Seq[Edge] = generateTestEdges(numberHighDegreeVertex, highVertexDegree)
    val expectedNumberBuckets: Long = 1L
    // Inserts all the test edges in parallel
    testEdges.par.foreach(sorter.addEdgeToResult)
    // Tests
    testSuite(
      sorter = sorter,
      unorderedEdgesSeq = testEdges,
      expectedNumberBuckets = expectedNumberBuckets
    )
  }
}
