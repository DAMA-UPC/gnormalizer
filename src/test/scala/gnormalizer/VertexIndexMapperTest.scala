package gnormalizer

import gnormalizer.Vertex.{InputVertex, VertexMapping}
import org.specs2.mutable.Specification

import scala.collection.immutable.HashMap

/**
  * Test for @see [[VertexIndexMapper]]
  */
class VertexIndexMapperTest extends Specification {

  "Test an empty mapper" should {
    val mapper: VertexIndexMapper = new VertexIndexMapper()
    "The vertex mapping size is equal to '0' (There are no elements)" in {
      mapper.numberMappings must beEqualTo(0)
    }
    "The vertexMapping() method returns an immutable empty map" in {
      mapper.initMappingStream.toMap must beEqualTo(Map[InputVertex, VertexMapping]())
    }
  }

  "Test a mapper generating a single element" should {
    val mapper: VertexIndexMapper = new VertexIndexMapper()
    val testVertex: String = "Single mapping test string"
    val index: VertexMapping = mapper.vertexMapping(testVertex)
    "The vertex mapping size is equal to '1' (There is a single vertex)" in {
      mapper.numberMappings must beEqualTo(1)
    }
    "The returned Vertex index must be '0'" in {
      index must beEqualTo(0)
    }
    "The vertexMapping() method returns a mapping with the inserted element" in {
      mapper.initMappingStream.toMap must beEqualTo(Map(testVertex -> 0))
    }
    "Return the same mapping if asking for the same element for a second time" in {
      val repitedIndex: VertexMapping = mapper.vertexMapping(testVertex)
      (repitedIndex must beEqualTo(index)) &&
        (mapper.initMappingStream.size must beEqualTo(1)) &&
        (mapper.initMappingStream.toMap must beEqualTo(Map(testVertex -> 0)))
    }
  }

  val numberVertex: Int = 10
  s"Test a mapper generating $numberVertex elements sequentially" should {
    val mapper: VertexIndexMapper = new VertexIndexMapper()
    val testVertex: Seq[InputVertex] = {
      (0 until numberVertex).map(i => s"Multiple mapping test String: $i")
    }
    val index: Seq[VertexMapping] = testVertex.map(mapper.vertexMapping)
    s"There must be '$numberVertex' stored mappings" in {
      mapper.numberMappings must beEqualTo(numberVertex)
    }
    s"Returned Vertex indexes must go from '0' until '$numberVertex'" in {
      index must beEqualTo(0 until numberVertex)
    }
    val mappingExpectation = {
      (0 until numberVertex).foldLeft(HashMap[InputVertex, VertexMapping]()) {
        (acc, i) => {
          acc + (testVertex(i) -> i)
        }
      }
    }
    "The vertexMapping() method returns a mapping with the inserted elements" in {
      mapper.initMappingStream.toMap must beEqualTo(mappingExpectation)
    }
    "Return the same mappings if asking for the same elements for a second time" in {
      val duplicatedIndexes: Seq[VertexMapping] = testVertex.map(mapper.vertexMapping)
      (duplicatedIndexes must beEqualTo(index)) &&
        (mapper.initMappingStream.size must beEqualTo(numberVertex)) &&
        (mapper.initMappingStream.toMap must beEqualTo(mappingExpectation))
    }
  }

  val numberParallelVertex: Int = 100000
  s"BRUTE FORCE TEST: Generates '$numberParallelVertex' elements in parallel" should {
    val mapper: VertexIndexMapper = new VertexIndexMapper()
    val testVertex: Seq[InputVertex] = {
      (0 until numberParallelVertex).map(i => s"Parallel test String $i")
    }
    val insertedIndexes: Seq[VertexMapping] = testVertex.par.map(mapper.vertexMapping).toList
    s"There must be '$numberParallelVertex' parallel inserted mappings" in {
      mapper.numberMappings must beEqualTo(numberParallelVertex)
    }
    s"The parallel inserted vertex indexes must go from '0' to '$numberParallelVertex'" in {
      insertedIndexes.sorted must beEqualTo(0 until numberParallelVertex)
    }
  }
}
