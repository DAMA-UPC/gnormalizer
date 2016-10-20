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
                              parallelInsertion: Boolean,
                              unorderedEdgesSeq: Seq[Edge],
                              expectedNumberBuckets: Long): Fragment = {
    // Inserts the edges
    (parallelInsertion match {
      case true => unorderedEdgesSeq.par
      case _ => unorderedEdgesSeq
    }).foreach(sorter.addEdgeToResult)

    // Expectations
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
            // Single bucket expectations
            testSuite(
              sorter = generateSorter(maxVerticesPerBucket = testBucketSize),
              parallelInsertion = false,
              unorderedEdgesSeq = generateTestEdges(testBucketSize, degree),
              expectedNumberBuckets = 1L
            )
          }
          "Test if the sorting is done as expected when using multiple buckets" should {
            val numVertex: Int = testBucketSize * 10

            // Multiple Bucket expectations
            testSuite(
              sorter = generateSorter(maxVerticesPerBucket = testBucketSize),
              parallelInsertion = false,
              unorderedEdgesSeq = generateTestEdges(numVertex, degree),
              expectedNumberBuckets = numVertex / testBucketSize
            )
          }
          s"Test if $numParallelVertices Vertex adjacency's can be done in parallel" should {
            val testEdges: Seq[Edge] = generateTestEdges(numParallelVertices, degree)
            val expectedNumberOfBuckets = numParallelVertices / defaultNumberVertexesPerBucket

            // Parallel test expectations
            testSuite(
              sorter = generateSorter(maxVerticesPerBucket = defaultNumberVertexesPerBucket),
              parallelInsertion = true,
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
    // High Degree Vertex Expectations:
    testSuite(
      sorter = generateSorter(maxVerticesPerBucket = defaultNumberVertexesPerBucket),
      parallelInsertion = true,
      unorderedEdgesSeq = generateTestEdges(numberHighDegreeVertex, highVertexDegree),
      expectedNumberBuckets = 1L
    )
  }
}
