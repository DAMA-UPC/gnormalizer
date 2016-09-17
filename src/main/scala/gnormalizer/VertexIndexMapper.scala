package gnormalizer

import gnormalizer.Vertex.{InputVertex, VertexMapping}

import scala.collection.immutable.{Stream => MemoryStream}
import scala.collection.mutable

/**
  * Trait containing the method used for retrieving the vertexes IDs. If a Vertex
  * ID was not already generated, it generates, stores and retrieve it instead.
  */
class VertexIndexMapper {

  private[this] val vertexMappingCache: mutable.Map[InputVertex, VertexMapping] = {
    mutable.HashMap.empty[InputVertex, VertexMapping]
  }

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
          val currentNumberVertexes = vertexMappingCache.size
          vertexMappingCache += (vertex -> currentNumberVertexes)
          currentNumberVertexes
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
}
