package parsers

import fs2.{Stream, Task}
import mappers.VertexIndexMapper
import models.Edge
import models.Vertex._

/**
  * [[Edge]] Parser for 'Edge List' inputs.
  */
object EdgeListParser extends EdgeParser {

  private[this] val vertexIndexMapper: VertexIndexMapper = new VertexIndexMapper()

  val commentedLinesStartCharacters: Seq[String] = Seq("#", "//")

  /**
    * @inheritdoc
    */
  override def toEdgeStream(inputStream: Stream[Task, String]): Stream[Task, Edge] = {
    inputStream
      .map(_.trim)
      // Removes all the commented or empty lines
      .filter(line => !line.isEmpty && !commentedLinesStartCharacters.exists(line.startsWith))
      .map(_.replaceAll("\\s+", " "))
      .map(parseEdge)
  }

  /**
    * Parses an input [[String]] into an [[Edge]].
    *
    * @param edgeString with each [[Edge]] [[models.Vertex]] separed by a whitespace.
    * @return the parsed [[Edge]].
    */
  @inline
  @SuppressWarnings(Array("org.wartremover.warts.Throw")) // FS2 manages failures with exceptions.
  private[this] def parseEdge(edgeString: String): Edge = {
    edgeString
      .trim // Removes the leading and the trailing spaces
      .replaceAll("\\s+", " ") // Removes
      .split(" ") match {
      case Array(sourceVertex: InputVertex, targetVertex: InputVertex) =>
        Edge(
          source = vertexIndexMapper.vertexMapping(sourceVertex),
          target = vertexIndexMapper.vertexMapping(targetVertex)
        )
      case invalidInput =>
        throw new IllegalArgumentException(
          s"Received ($invalidInput) on EdgeListParser, while the expected format is: 'a b'"
        )
    }
  }
}
