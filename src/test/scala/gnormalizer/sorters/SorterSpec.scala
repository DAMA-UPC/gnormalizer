package gnormalizer.sorters

import babel.graph.{Edge, Node}
import org.specs2.mutable.Specification
import org.specs2.specification.core.{Fragment, Fragments}

import scala.util.Random

/**
  * Base test for all the implementations from: @see [[Sorter]]
  */
trait SorterSpec extends Specification {

  /**
    * Generates a [[Sorter]] that will be used for the [[SorterSpec]] tests.
    */
  def generateSorter(maxNodesPerBucket: Int): Sorter

  /**
    * Obtains the default maximum amount of source [[Node]]s
    * in a specific [[Sorter]] implementation.
    */
  def defaultNumberNodesPerBucket: Int

  /**
    * Generates a randomized list of test edges.
    *
    * @param numberNodes which nodes will be generated.
    * @param graphDegree  of the graph. All the [[Node]] will have the @param
    *                     number of output [[Edge]]s.
    * @return a randomized sequence containing all he
    */
  private[this] def generateTestEdges(numberNodes: Int, graphDegree: Int): Seq[Edge] = {
    Random
      .shuffle(
        (0 until numberNodes)
          .map(node => (0 until graphDegree).map(degree => Edge(node, degree)))
          .foldLeft(Seq[Edge]())(_ ++ _)
      )
      .toList
  }

  /**
    * Generates all the required test cases for any test case used in this class.
    */
  protected def baseTestFragments(sorter: Sorter,
                                  parallelInsertion: Boolean,
                                  unorderedEdgesSeq: Seq[Edge],
                                  expectedNumberBuckets: Long): Fragment = {
    // Inserts the edges
    (if (parallelInsertion) {
       unorderedEdgesSeq.par
     } else {
       unorderedEdgesSeq
     }).foreach(sorter.addEdgeToResult)

    // Expectations
    s"Check if the expected '$expectedNumberBuckets' buckets where used" in {
      sorter.countNumberBuckets() must beEqualTo(expectedNumberBuckets)
    }
    s"The result must contain ${unorderedEdgesSeq.size} elements" in {
      sorter.countNumberEdges() must beEqualTo(unorderedEdgesSeq.size)
    }
    s"The ${unorderedEdgesSeq.size} nodes must be properly sorted" in {
      sorter.resultStream().equals(unorderedEdgesSeq.sortWith(_.compareTo(_) < 0))
    }
  }

  @inline private[this] val testBucketSize: Int = 50
  @inline private[this] val maxParallelNodes: Int = 10000
  @inline private[this] val graphDegreeToTest1: Int = 1
  @inline private[this] val graphDegreeToTest2: Int = 25
  Seq(graphDegreeToTest1, graphDegreeToTest2).foldLeft(Fragments()) { (fragments, degree) =>
    {
      fragments.append(
        s"Inserts and sort graphs with degree: '$degree'" should {
          // Single bucket expectations
          "Test if the sorting is done as expected when using a single bucket" should {
            baseTestFragments(sorter = generateSorter(maxNodesPerBucket = testBucketSize),
                              parallelInsertion = false,
                              unorderedEdgesSeq = generateTestEdges(testBucketSize, degree),
                              expectedNumberBuckets = 1L)
          }
          // Multiple Bucket expectations
          "Test if the sorting is done as expected when using multiple buckets" should {
            val bucketSize = 10
            val numNodes: Int = bucketSize * 5
            val expectedNumberBuckets = numNodes / bucketSize

            baseTestFragments(sorter = generateSorter(maxNodesPerBucket = bucketSize),
                              parallelInsertion = false,
                              unorderedEdgesSeq = generateTestEdges(numNodes, degree),
                              expectedNumberBuckets = expectedNumberBuckets)
          }
          // Parallel test expectations
          s"Test if $maxParallelNodes Node adjacency's can be done in parallel" should {
            val testEdges: Seq[Edge] = generateTestEdges(maxParallelNodes, degree)
            val expectedNumberOfBuckets = maxParallelNodes / defaultNumberNodesPerBucket

            baseTestFragments(sorter =
                                generateSorter(maxNodesPerBucket = defaultNumberNodesPerBucket),
                              parallelInsertion = true,
                              unorderedEdgesSeq = testEdges,
                              expectedNumberBuckets = expectedNumberOfBuckets)
          }
        }
      )
    }
  }
  val highNodeDegrees: Int = maxParallelNodes / 2
  val numberHighDegreeNodes: Int = 5
  s"With '$numberHighDegreeNodes' nodes test a graph degree of '$highNodeDegrees'" in {
    // High Degree Nodes Expectations:
    baseTestFragments(sorter = generateSorter(maxNodesPerBucket = defaultNumberNodesPerBucket),
                      parallelInsertion = true,
                      unorderedEdgesSeq = generateTestEdges(numberHighDegreeNodes, highNodeDegrees),
                      expectedNumberBuckets = 1L)
  }
}
