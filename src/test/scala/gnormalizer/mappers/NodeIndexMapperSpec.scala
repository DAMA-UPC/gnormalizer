package gnormalizer.mappers

import babel.graph.{Node, NodeMapping}
import gnormalizer.mappers.NodeIndexMapper.Mapping
import org.specs2.mutable.Specification

/**
  * Test for @see [[NodeIndexMapper]]
  */
class NodeIndexMapperSpec extends Specification {

  "Test an empty mapper" should {
    val mapper: NodeIndexMapper = new NodeIndexMapper()
    "The node mapping size is equal to '0' (There are no elements)" in {
      mapper.numberMappings must beEqualTo(0)
    }
    "The initMappingStream() method returns an immutable empty map" in {
      mapper.initMappingStream.toList must beEqualTo(List[NodeMapping]())
    }
  }

  "Test a mapper generating a single element" should {
    val mapper: NodeIndexMapper = new NodeIndexMapper()
    val testNode: String = "Single mapping test string"
    val index: NodeMapping = mapper.nodeMapping(testNode)
    "The node mapping size is equal to '1' (There is a single node)" in {
      mapper.numberMappings must beEqualTo(1)
    }
    "The returned Node index must be '0'" in {
      index must beEqualTo(0)
    }
    "The initMappingStream() method returns a mapping with the inserted element" in {
      mapper.initMappingStream.toList must beEqualTo(List(Mapping(testNode, 0)))
    }
    "Return the same mapping if asking for the same element for a second time" in {
      val repeatedIndex: NodeMapping = mapper.nodeMapping(testNode)
      (repeatedIndex must beEqualTo(index)) &&
      (mapper.initMappingStream.size must beEqualTo(1)) &&
      (mapper.initMappingStream.toList must beEqualTo(List(Mapping(testNode, 0))))
    }
  }

  val numberNodes: Int = 10
  s"Test a mapper generating $numberNodes elements sequentially" should {
    val mapper: NodeIndexMapper = new NodeIndexMapper()
    val testNode: Seq[Node] = {
      (0 until numberNodes).map(i => s"Multiple mapping test String: $i")
    }
    val index: Seq[NodeMapping] = testNode.map(mapper.nodeMapping)
    s"There must be '$numberNodes' stored mappings" in {
      mapper.numberMappings must beEqualTo(numberNodes)
    }
    s"Returned Node indexes must go from '0' until '$numberNodes'" in {
      index must beEqualTo(0 until numberNodes)
    }
    val mappingExpectation = {
      (0 until numberNodes).foldLeft(List[Mapping]()) { (acc, i) =>
        {
          acc :+ Mapping(testNode(i), i)
        }
      }
    }
    "The initMappingStream() method returns a mapping with the inserted elements" in {
      mapper.initMappingStream.toList must beEqualTo(mappingExpectation)
    }
    "Return the same mappings if asking for the same elements for a second time" in {
      val duplicatedIndexes: Seq[NodeMapping] = testNode.map(mapper.nodeMapping)
      (duplicatedIndexes must beEqualTo(index)) &&
      (mapper.initMappingStream.size must beEqualTo(numberNodes)) &&
      (mapper.initMappingStream.toList must beEqualTo(mappingExpectation))
    }
  }

  val numberEqualParallelNodes: Int = 100000
  s"Manages in parallel' $numberEqualParallelNodes' petitions from same element" should {
    val mapper: NodeIndexMapper = new NodeIndexMapper()
    val repeatedElement: Node = "Element to repeat multiple times"
    val element = {
      (0 until numberEqualParallelNodes).map(_ => mapper.nodeMapping(repeatedElement))
    }

    s"All petitions returned the same index: '0'" in {
      element.forall(_ == 0)
    }
    s"There must be a single mapping in the mapper" in {
      mapper.numberMappings must beEqualTo(1)
    }
    "The initMappingStream() method returns a map with only one mapping" in {
      val expectedMappings = Seq(Mapping(repeatedElement, 0L))
      mapper.initMappingStream.toList must beEqualTo(expectedMappings)
    }
  }

  val numberDifferentParallelNode: Int = 100000
  s"Maps in parallel '$numberDifferentParallelNode' elements" should {
    val mapper: NodeIndexMapper = new NodeIndexMapper()
    val testNode: Seq[Node] = {
      (0 until numberDifferentParallelNode).map(i => s"Parallel test String $i")
    }
    val insertedIndexes: Seq[NodeMapping] = testNode.par.map(mapper.nodeMapping).toList
    s"There must be '$numberDifferentParallelNode' parallel inserted mappings" in {
      mapper.numberMappings must beEqualTo(numberDifferentParallelNode)
    }
    s"Parallel inserted node indexes must go from '0' to '$numberDifferentParallelNode'" in {
      insertedIndexes.sorted must beEqualTo(0 until numberDifferentParallelNode)
    }
  }
}
