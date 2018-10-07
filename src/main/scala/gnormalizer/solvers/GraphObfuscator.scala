package gnormalizer
package solvers

import babel.graph.Edge
import better.files._
import cats.effect.IO
import gnormalizer.io.FileDataSourceHandler
import gnormalizer.parsers.EdgeListParser
import gnormalizer.sorters.{Sorter, SorterSelector}

/**
  * Object containing a set of methods used for obfuscating an input graph.
  */
object GraphObfuscator {

  /**
    * Class used for normalizing the graph found at the input stream.
    *
    * @param path       containing the path to the graph being obfuscated.
    * @param startDeserializationAtLine containing the line number where parsing starts.
    */
  @SuppressWarnings(Array("org.wartremover.warts.Nothing", "org.wartremover.warts.Any"))
  def obfuscateEdgeListGraph(path: String,
                             startDeserializationAtLine: Long = 0L): IO[Stream[Edge]] = {

    val resultManager: Sorter = SorterSelector.sorterFromFile(path.toFile.size)

    // Initializes a file content Stream from the source.
    val input: fs2.Stream[IO, String] =
      new FileDataSourceHandler().init(path).drop(startDeserializationAtLine)

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
