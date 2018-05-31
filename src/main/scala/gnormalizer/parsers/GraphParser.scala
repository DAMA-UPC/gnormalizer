package gnormalizer.parsers

import babel.graph.{Edge, Vertex}
import cats.effect.IO
import gnormalizer.mappers.VertexIndexMapper
import gnormalizer.mappers.VertexIndexMapper.Mapping

/**
  * Base trait for all [[Edge]] parsers.
  */
trait GraphParser {

  protected val vertexIndexMapper: VertexIndexMapper = new VertexIndexMapper()

  /**
    * Converts all the input [[String]]s from a [[Stream]] into [[Edge]]s.
    *
    * @param inputStream which content is going to be transformed.
    * @return a [[Stream]] of [[Edge]]s.
    */
  def toEdgeStream(inputStream: fs2.Stream[IO, String]): fs2.Stream[IO, Edge]

  /**
    * Initializes an [[Stream]] specifying the the [[Vertex]] [[Mapping]]s
    * from all the elements that have been converted calling [[GraphParser.toEdgeStream]].
    *
    * @return an [[Stream]] containing all the [[Vertex]] mappings.
    */
  def mappingsStream(): Stream[Mapping] = vertexIndexMapper.initMappingStream

}
