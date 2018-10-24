package gnormalizer
package normalizers

import babel.graph.Edge
import better.files._
import cats.effect.IO
import gnormalizer.io.FileDataSourceHandler
import gnormalizer.parsers.EdgeListParser
import gnormalizer.sorters.{Sorter, SorterSelector}

/**
  * Object containing a set of methods used for normalizing an input graph.
  */
class GraphNormalizer {

  /**
    * Class used for normalizing the graph found at the input stream.
    *
    * @param path       containing the path to the graph being normalized.
    * @param startDeserializationAtLine containing the line number where parsing starts.
    */
  @SuppressWarnings(Array("org.wartremover.warts.Nothing", "org.wartremover.warts.Any"))
  def obfuscateEdgeListGraph(path: String,
                             startDeserializationAtLine: Option[Long] = None): IO[Stream[Edge]] = {

    val resultManager: Sorter = SorterSelector.sorterFromFile(path.toFile.size)

    // Initializes a file content Stream from the source.
    val input: fs2.Stream[IO, String] =
      new FileDataSourceHandler()
        .init(path)
        .drop(Math.max(0, startDeserializationAtLine.map(_ - 1L).getOrElse(0L)))

    // Converts the input Stream into an Edge Stream.
    val edgeStream: fs2.Stream[IO, Edge] = new EdgeListParser().toEdgeStream(input)

    // Orders all the input Edges and builds a result Stream.
    edgeStream
      .map(resultManager.addEdgeToResult)
      .compile
      .drain
      .map(_ => resultManager.resultStream())
  }
}
