package gnormalizer.io

import java.nio.file.Paths

import fs2.{Task, io, text, Stream => FileStream}

/**
  * Object containing a set of methods used for managing file [[DataSourceHandler]].
  */
class FileDataSourceHandler extends DataSourceHandler {

  /**
    * @inheritdoc
    */
  @inline
  override def init(path: String): FileStream[Task, String] = {
    io.file
      .readAll[Task](Paths.get(path), FileDataSourceHandler.chunkSize)
      .through(text.utf8Decode)
      .through(text.lines)
  }
}

object FileDataSourceHandler {

  /**
    * Maximum size of the data chunks being
    */
  @inline val chunkSize: Int = 4096
}
