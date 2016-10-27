package parsers

import fs2.Task
import mappers.VertexIndexMapper
import mappers.VertexIndexMapper.Mapping
import models.Edge

/**
  * Object representing an [[Edge]] parser.
  */
trait EdgeParser {

  protected val vertexIndexMapper: VertexIndexMapper = new VertexIndexMapper()

  /**
    * Converts all the input [[String]]s from a [[Stream]] into [[Edge]]s.
    *
    * @param inputStream which content is going to be transformed.
    * @return a [[Stream]] of [[Edge]]s.
    */
  def toEdgeStream(inputStream: fs2.Stream[Task, String]): fs2.Stream[Task, Edge]

  /**
    * Initializes an [[Stream]] specifying the output Str
    * @return
    */
  def mappingsStream() : Stream[Mapping] = vertexIndexMapper.initMappingStream

}
