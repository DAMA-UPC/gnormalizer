package gnormalizer

import gnormalizer.Vertex.Vertex
import org.specs2.mutable.Specification

import scala.collection.immutable.HashMap

/**
  * Test for @see [[VertexIndexMapper]]
  */
class VertexIndexMapperTest extends Specification {

  "Test an empty mapper" should {
    val mapper: VertexIndexMapper = new VertexIndexMapper()
    "The vertex mapping size is equal to '0' (There are no elements)" in {
      mapper.vertexMapping.size must beEqualTo(0)
    }
    "The vertexMapping() method returns an immutable empty map" in {
      mapper.vertexMapping must beEqualTo(Map[String, Vertex]())
    }
  }

  "Test a mapper generating a single element" should {
    val mapper: VertexIndexMapper = new VertexIndexMapper()
    val testString: String = "test string"
    val index: Vertex = mapper.vertexIndex(testString)
    "The vertex mapping size is equal to '1' (There is a single vertex)" in {
      mapper.vertexMapping.size must beEqualTo(1)
    }
    "The returned Vertex index should be '0'" in {
      index must beEqualTo(0)
    }
    "The vertexMapping() method returns a mapping with the inserted element" in {
      mapper.vertexMapping must beEqualTo(Map(testString -> 0))
    }
    "Return the same mapping if asking for the same element for a second time" in {
      val repitedIndex: Vertex = mapper.vertexIndex(testString)
      (repitedIndex must beEqualTo(index)) &&
        (mapper.vertexMapping.size must beEqualTo(1)) &&
        (mapper.vertexMapping must beEqualTo(Map(testString -> 0)))
    }
  }

  val numberVertex: Int = 10
  s"Test a mapper generating $numberVertex elements sequentially" should {
    val mapper: VertexIndexMapper = new VertexIndexMapper()
    val testStrings: Seq[String] = (0 until numberVertex).map(i => s"test string $i")
    val index: Seq[Vertex] = testStrings.map(mapper.vertexIndex)
    s"There must be '$numberVertex' stored mappings" in {
      mapper.vertexMapping.size must beEqualTo(numberVertex)
    }
    s"The returned Vertex index should be from '0' until '$numberVertex'" in {
      index must beEqualTo(0 until numberVertex)
    }
    val mappingExpectation = {
      (0 until numberVertex).foldLeft(HashMap[String, Vertex]()) {
        (acc, i) => {
          acc + (testStrings(i) -> i)
        }
      }
    }
    "The vertexMapping() method returns a mapping with the inserted elements" in {
      mapper.vertexMapping must beEqualTo(mappingExpectation)
    }
    "Return the same mappings if asking for the same elements for a second time" in {
      val duplicatedIndexes: Seq[Vertex] = testStrings.map(mapper.vertexIndex)
      (duplicatedIndexes must beEqualTo(index)) &&
        (mapper.vertexMapping.size must beEqualTo(numberVertex)) &&
        (mapper.vertexMapping must beEqualTo(mappingExpectation))
    }
  }

  val numberParallelVertex: Int = 100000
  s"BRUTE FORCE TEST: Generates '$numberParallelVertex' elements in parallel" should {
    val mapper: VertexIndexMapper = new VertexIndexMapper()
    val testStrings: Seq[String] = {
      (0 until numberParallelVertex).map(i => s"Parallel test String $i")
    }
    val insertedIndexes: Seq[Vertex] = testStrings.par.map(mapper.vertexIndex).toList
    s"There must be '$numberParallelVertex' parallel inserted mappings" in {
      mapper.vertexMapping.size must beEqualTo(numberParallelVertex)
    }
    s"The parallel inserted vertex indexes must go from '0' to '$numberParallelVertex'" in {
      insertedIndexes.sorted must beEqualTo(0 until numberParallelVertex)
    }
  }
}
