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
    * Obtains the default maximum amount of [[gnormalizer.models.Vertex#source]]
    * in a specific [[Sorter]] implementation.
    */
  def defaultNumberVertexesPerBucket : Int

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
            val bucketSize: Int = degree * testBucketSize
            val sorter = generateSorter(testBucketSize)
            val testEdges: Seq[Edge] = generateTestEdges(numberTestEdges, degree)
            // Inserts the edges sequentially
            testEdges.foreach(sorter.addEdgeToResult)
            "Use only one bucket was used for doing the sorting" in {
              sorter.countNumberBuckets() must beEqualTo(1)
            }
            s"The result must have ${numberTestEdges * degree} elements in one bucket" in {
              sorter.countNumberEdges() must beEqualTo(numberTestEdges * degree)
            }
            s"Sorts the inserted $numberTestEdges edges as expected" in {
              testEdges.sortWith(_.compareTo(_) < 0).
                equals(sorter.resultStream().toList) must beTrue
            }
          }
          "Test if the sorting is done as expected when using multiple buckets" should {
            val numVertex: Int = testBucketSize * 10
            val sorter = generateSorter(testBucketSize)
            val testEdges: Seq[Edge] = generateTestEdges(numVertex, degree)
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
              testEdges.sortWith(_.compareTo(_) < 0).equals(
                sorter.resultStream().toIndexedSeq
              ) must beTrue
            }
          }
          s"Test if $numParallelVertices Vertex adjacency's can be done in parallel" should {
            val sorter = generateSorter(defaultNumberVertexesPerBucket)
            val testEdges: Seq[Edge] = generateTestEdges(numParallelVertices, degree)
            val expectedNumberOfBuckets = numParallelVertices / defaultNumberVertexesPerBucket
            // Inserts the edges in parallel
            testEdges.par.foreach(sorter.addEdgeToResult)
            s"$expectedNumberOfBuckets buckets were used for doing the sorting in parallel" in {
              sorter.countNumberBuckets() must beEqualTo(expectedNumberOfBuckets)
            }
            s"The result must have ${numParallelVertices * degree} parallel inserted elements" in {
              sorter.countNumberEdges() must beEqualTo(numParallelVertices * degree)
            }
            s"Sorts the ${numParallelVertices * degree} parallel inserted edges as expected" in {
              sorter.resultStream().toIndexedSeq.equals(
                testEdges.sortWith(_.compareTo(_) < 0)
              ) must beTrue
            }
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
    // Inserts all the test edges in parallel
    testEdges.par.foreach(sorter.addEdgeToResult)
    s"Only one bucket was used for doing the sorting in parallel" in {
      sorter.countNumberBuckets() must beEqualTo(1L)
    }
    s"The result must contain ${numberHighDegreeVertex * highVertexDegree} elements" in {
      sorter.countNumberEdges() must beEqualTo(numberHighDegreeVertex * highVertexDegree)
    }
    s"The ${numberHighDegreeVertex * highVertexDegree} vertices must be sorter in parallel" in {
      sorter.resultStream().toIndexedSeq.equals(
        testEdges.sortWith(_.compareTo(_) < 0)
      ) must beTrue
    }
  }
}
