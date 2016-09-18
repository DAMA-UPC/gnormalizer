package gnormalizer

import gnormalizer.Vertex.{InputVertex, VertexMapping}

import scala.collection.immutable.{Stream => MemoryStream}
import scala.collection.mutable

/**
  * Trait containing the method used for retrieving the vertexes IDs. If a Vertex
  * ID was not already generated, it generates, stores and retrieve it instead.
  *
  * This class can map a maximum of [[Integer.MAX_VALUE]] values.
  */
final class VertexIndexMapper {

  // NOTE: In spite mutability is not a practice following functional
  // programming practices, this class implements several mutable values,
  // in order to improve the application performance.

  private[this] val vertexMappingCache: mutable.Map[InputVertex, VertexMapping] = {
    mutable.HashMap.empty[InputVertex, VertexMapping]
  }

  private[this] var mappingCacheSize : Long = 0

  /**
    * Returns the cache vertex Index if available. Generates a new one and returns it otherwise.
    *
    * @param vertex from the index that will be retrieved.
    * @return [[Vertex]] with the resulting index. (Starting from 0).
    */
  def vertexMapping(vertex: InputVertex): VertexMapping = {
    vertexMappingCache.get(vertex) match {
      case Some(index) => index
      case _ =>
        vertexMappingCache.synchronized {
          vertexMappingCache += (vertex -> mappingCacheSize.toInt)
          mappingCacheSize += 1
          mappingCacheSize.toInt - 1
        }
    }
  }

  /**
    * Obtains the mapping from all the indexed [[Vertex]] as
    * a [[MemoryStream[[(Vertex, VertexMappingId)]]]].
    */
  def initMappingStream: MemoryStream[(InputVertex, VertexMapping)] = {
    vertexMappingCache.toStream
  }

  /**
    * Obtains the number of mappings done by the mapper.
    * @return [[Long]] with the number of mappings.
    */
  def numberMappings : Long = mappingCacheSize
}
