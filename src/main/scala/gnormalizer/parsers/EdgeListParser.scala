package gnormalizer.parsers

import babel.graph.{Edge, Node}
import cats.effect.IO
import fs2.Stream

/**
  * [[Edge]] Parser for 'Edge List' inputs.
  */
class EdgeListParser extends GraphParser {

  private[parsers] val commentedLinesStartCharacters: Seq[String] = Seq("#", "//")

  /**
    * @inheritdoc
    */
  override def toEdgeStream(inputStream: Stream[IO, String]): Stream[IO, Edge] = {
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
    * @param edgeString with each [[Edge]] [[Node]] separed by a whitespace.
    * @return the parsed [[Edge]].
    */
  @inline
  @throws[IllegalArgumentException]
  @SuppressWarnings(Array("org.wartremover.warts.Throw")) // FS2 manages failures with exceptions.
  protected def parseEdge(edgeString: String): Edge = {
    edgeString
      .split(" ") match {
      case Array(sourceNode: Node, targetNode: Node) =>
        Edge(source = nodeIndexMapper.nodeMapping(sourceNode),
             target = nodeIndexMapper.nodeMapping(targetNode))
      case invalidInput =>
        throw new IllegalArgumentException(
          s"Received ($invalidInput) on EdgeListParser, while the expected format is: 'a b'"
        )
    }
  }
}
