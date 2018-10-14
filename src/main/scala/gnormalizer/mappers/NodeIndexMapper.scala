package gnormalizer.mappers

import babel.graph.Node
import babel.graph.NodeMapping

import scala.collection.immutable.{Stream => MemoryStream}
import scala.collection.mutable

/**
  * Trait containing the method used for retrieving the node IDs. If a Vertex
  * ID was not already generated, it generates, stores and retrieve it instead.
  *
  * This class can map a maximum of [[Integer.MAX_VALUE]] values.
  */
final class NodeIndexMapper {

  private[this] val nodeMappingCache: mutable.Map[Node, NodeMapping] =
    mutable.HashMap.empty[Node, NodeMapping]

  private[this] var mappingCacheSize: Long = 0

  /**
    * Returns the cache [[Node]] Index if available. Generates a new one and returns it otherwise.
    *
    * @param node from the index that will be retrieved.
    * @return [[Node]] with the resulting index. (Starting from 0).
    */
  def nodeMapping(node: Node): NodeMapping = {
    nodeMappingCache.get(node) match {
      case Some(index) => index
      case _ =>
        nodeMappingCache.synchronized {
          // The second level of nesting is done in order to fix a race condition issue
          // when two elements arrive at the synchronized block simultaneously.
          nodeMappingCache.get(node) match {
            case Some(index) => index
            case _ =>
              nodeMappingCache += (node -> mappingCacheSize)
              mappingCacheSize += 1
              mappingCacheSize - 1
          }
        }
    }
  }

  /**
    * Obtains the mapping from all the indexed [[Node]] as
    * a [[MemoryStream[[(Node, NodeMappingId)]].
    */
  def initMappingStream: MemoryStream[NodeIndexMapper.Mapping] = {
    nodeMappingCache.toSeq.map(NodeIndexMapper.Mapping.tupled).sortBy(_.mappedAs).toStream
  }

  /**
    * Obtains the number of mappings done by the mapper.
    *
    * @return [[Long]] with the number of mappings.
    */
  def numberMappings: Long = mappingCacheSize
}

/**
  * Companion object for @see [[NodeIndexMapper]]
  */
object NodeIndexMapper {

  /**
    * Represents the mapping done to a [[Node]].
    */
  case class Mapping(input: Node, mappedAs: NodeMapping)

}
