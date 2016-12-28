package io

import java.nio.file.Paths

import fs2.{Task, io, text, Stream => FileStream}

/**
  * Object containing a set of methods used for managing file [[DataSourceHandler]].
  */
class FileDataSourceHandler extends DataSourceHandler {

  @inline val chunkSize: Int = 4096

  /**
    * @inheritdoc
    */
  @inline
  override def init(path: String): FileStream[Task, String] = {
    io
      .file
      .readAll[Task](Paths.get(path), chunkSize)
      .through(text.utf8Decode)
      .through(text.lines)
  }
}
