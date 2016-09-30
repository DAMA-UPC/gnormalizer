package gnormalizer.io

import java.nio.file.Paths

import fs2.{Task, io, text, Stream => FileStream}

/**
  * Object containing a set of methods used for initializing file [[FileStream]].
  */
object FileStreamInitializer {

  @inline val defaultChunkSize: Int = 4096

  /**
    * Initializes a [[FileStream]] of an input file.
    *
    * @param path      where the [[FileStream]] will start.
    * @param chunkSize from the [[FileStream]].
    * @return a [[FileStream]] that will be return the graph contents as UTF-8 [[String]]'s.
    */
  @inline
  def initializeFileStream(path: String,
                           chunkSize: Int = defaultChunkSize): FileStream[Task, String] = {
    io
      .file
      .readAll[Task](Paths.get(path), chunkSize)
      .through(text.utf8Decode)
  }
}
