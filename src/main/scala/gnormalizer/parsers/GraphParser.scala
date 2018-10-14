package gnormalizer.parsers

import babel.graph.{Edge, Node}
import cats.effect.IO
import gnormalizer.mappers.NodeIndexMapper
import gnormalizer.mappers.NodeIndexMapper.Mapping

/**
  * Base trait for all [[Edge]] parsers.
  */
trait GraphParser {

  protected val nodeIndexMapper: NodeIndexMapper = new NodeIndexMapper()

  /**
    * Converts all the input [[String]]s from a [[Stream]] into [[Edge]]s.
    *
    * @param inputStream which content is going to be transformed.
    * @return a [[Stream]] of [[Edge]]s.
    */
  def toEdgeStream(inputStream: fs2.Stream[IO, String]): fs2.Stream[IO, Edge]

  /**
    * Initializes an [[Stream]] specifying the the [[Node]] [[Mapping]]s
    * from all the elements that have been converted calling [[GraphParser.toEdgeStream]].
    *
    * @return an [[Stream]] containing all the [[Node]] mappings.
    */
  def mappingsStream(): Stream[Mapping] = nodeIndexMapper.initMappingStream

}
