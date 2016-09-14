package gnormalizer

import gnormalizer.Vertex.Vertex

import scala.collection.{immutable, mutable}

/**
  * Trait containing the method used for retrieving the vertexes IDs. If a Vertex
  * ID was not already generated, it generates, stores and retrieve it instead.
  */
class VertexIndexMapper {

  private[this] val vertexMappingCache: mutable.Map[String, Vertex] = {
    mutable.HashMap.empty[String, Vertex]
  }

  /**
    * Returns the cache vertex Index if available. Generates a new one and returns it otherwise.
    *
    * @param vertex from the index that will be retrieved.
    * @return [[Vertex]] with the resulting index. (Starting from 0).
    */
  def vertexIndex(vertex: String): Vertex = {
    vertexMappingCache.get(vertex) match {
      case Some(index) => index
      case _ =>
        vertexMappingCache.synchronized {
          val currentNumberVertixes = vertexMappingCache.size
          vertexMappingCache += (vertex -> currentNumberVertixes)
          currentNumberVertixes
        }
    }
  }

  /**
    * Obtains the mapping from all the indexed [[Vertex]] as an [[immutable.Map[[String, Vertex]]]]
    */
  def vertexMapping: immutable.Map[String, Vertex] = vertexMappingCache.toMap
}
