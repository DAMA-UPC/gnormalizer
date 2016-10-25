package sorters

import models.Edge
import org.specs2.mutable.Specification
import org.specs2.specification.core.{Fragment, Fragments}

import scala.util.Random

/**
  * Base test for all the implementations from: @see [[Sorter]]
  */
trait SorterTest extends Specification {

  /**
    * Generates a [[Sorter]] that will be used for the [[SorterTest]] tests.
    */
  def generateSorter(maxVerticesPerBucket: Int): Sorter

  /**
    * Obtains the default maximum amount of [[models.Vertex#source]]
    * in a specific [[Sorter]] implementation.
    */
  def defaultNumberVertexesPerBucket: Int

  /**
    * Generates a randomized list of test edges.
    *
    * @param numberVertex which vertices will be generated.
    * @param graphDegree  of the graph. All the [[models.Vertex]] will have the @param
    *                     number of output [[Edge]]s.
    * @return a randomized sequence containing all he
    */
  private[this] def generateTestEdges(numberVertex: Int, graphDegree: Int): Seq[Edge] = {
    Random.shuffle(
      (0 until numberVertex).
        map(
          vertex =>
            (0 until graphDegree).
              map(degree => Edge(vertex, degree))
        )
        .reduce(_ ++ _)
    ).toList
  }

  /**
    * Generates all the required test cases for any test case used in this class.
    */
  protected def baseTestFragments(sorter: Sorter,
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
          // Single bucket expectations
          "Test if the sorting is done as expected when using a single bucket" should {
            baseTestFragments(
              sorter = generateSorter(maxVerticesPerBucket = testBucketSize),
              parallelInsertion = false,
              unorderedEdgesSeq = generateTestEdges(testBucketSize, degree),
              expectedNumberBuckets = 1L
            )
          }
          // Multiple Bucket expectations
          "Test if the sorting is done as expected when using multiple buckets" should {
            val bucketSize = 10
            val numVertex: Int = bucketSize * 5
            val expectedNumberBuckets = numVertex / bucketSize

            baseTestFragments(
              sorter = generateSorter(maxVerticesPerBucket = bucketSize),
              parallelInsertion = false,
              unorderedEdgesSeq = generateTestEdges(numVertex, degree),
              expectedNumberBuckets = expectedNumberBuckets
            )
          }
          // Parallel test expectations
          s"Test if $numParallelVertices Vertex adjacency's can be done in parallel" should {
            val testEdges: Seq[Edge] = generateTestEdges(numParallelVertices, degree)
            val expectedNumberOfBuckets = numParallelVertices / defaultNumberVertexesPerBucket

            baseTestFragments(
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
    baseTestFragments(
      sorter = generateSorter(maxVerticesPerBucket = defaultNumberVertexesPerBucket),
      parallelInsertion = true,
      unorderedEdgesSeq = generateTestEdges(numberHighDegreeVertex, highVertexDegree),
      expectedNumberBuckets = 1L
    )
  }
}
