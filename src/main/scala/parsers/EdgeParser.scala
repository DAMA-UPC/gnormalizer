package parsers

import fs2.{Stream, Task}
import models.Edge

/**
  * Object representing an [[Edge]] parser.
  */
trait EdgeParser {

  /**
    * Converts all the input [[String]]s from a [[Stream]] into [[Edge]]s.
    *
    * @param inputStream which content is going to be transformed.
    * @return a [[Stream]] of [[Edge]]s.
    */
  def toEdgeStream(inputStream: Stream[Task, String]): Stream[Task, Edge]

}
