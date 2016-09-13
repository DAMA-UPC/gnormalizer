package gnormalizer

import cats.Id
import gnormalizer.Vertex.Vertex

import scala.collection.{immutable, mutable}

/**
  * Trait containing the method used for storing/generating the vertexes IDs.
  */
class VertexIndexGenerator {

  private[this] val vertexMappingCache : mutable.Map[String, Vertex] = {
    mutable.HashMap.empty[String, Vertex]
  }

  /**
    * Returns the cache vertex Index if available. Generates a new one and returns it otherwise.
    * @param vertex from the index that will be retrieved.
    * @return [[Long]] with the resulting index. (Starting from 0).
    */
  def vertexIndex(vertex : String) : Vertex = {
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
    * Obtains the mapping from all the indexed Vertex [[Id]].
    */
  def vertexMapping : immutable.Map[String, Vertex] = vertexMappingCache.toMap

  /**
    * @return the number of indexed Vertexes.
    */
  def countIndexedVertex : Int = vertexMappingCache.size
}
