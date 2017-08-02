package gnormalizer.parsers

import babel.graph.Vertex
import babel.graph.Edge
import fs2.{Stream, Task}

/**
  * [[Edge]] Parser for 'Edge List' inputs.
  */
class EdgeListParser extends GraphParser {

  val commentedLinesStartCharacters: Seq[String] = Seq("#", "//")

  /**
    * @inheritdoc
    */
  override def toEdgeStream(inputStream: Stream[Task, String]): Stream[Task, Edge] = {
    inputStream
      .map(_.trim) // Removes the leading and the trailing spaces
      .map(_.replaceAll("\\s+", " ")) // Converts consecutive whitespaces into a single one
      // Removes all the commented or empty lines
      .filter(line => !line.isEmpty && !commentedLinesStartCharacters.exists(line.startsWith))
      .map(parseEdge)
  }

  /**
    * Parses an input [[String]] into an [[Edge]].
    *
    * @param edgeString with each [[Edge]] [[Vertex]] separed by a whitespace.
    * @return the parsed [[Edge]].
    */
  @inline
  @throws[IllegalArgumentException]
  @SuppressWarnings(Array("org.wartremover.warts.Throw")) // FS2 manages failures with exceptions.
  protected def parseEdge(edgeString: String): Edge = {
    edgeString
      .split(" ") match {
      case Array(sourceVertex: Vertex, targetVertex: Vertex) =>
        Edge(source = vertexIndexMapper.vertexMapping(sourceVertex),
             target = vertexIndexMapper.vertexMapping(targetVertex))
      case invalidInput =>
        throw new IllegalArgumentException(
          s"Received ($invalidInput) on EdgeListParser, while the expected format is: 'a b'"
        )
    }
  }
}
