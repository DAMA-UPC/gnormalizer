package gnormalizer.io

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, StandardOpenOption}

import gnormalizer.models.Edge

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Set of utilities used for written [[Edge]]s to the hard disk.
  */
object FileManager {

  /**
    * Creates a directory in the input folder in case it does not exist.
    *
    * @param path where the directory will be created.
    */
  @inline
  def makeDir(path: String): Unit = new File(path).mkdirs()

  /**
    * Removes a directory and all the files within it.
    * @param file from the directory that will be removed from the disk.
    */
  @inline
  def removeDir(file: File): Unit = {
    if (file.isDirectory) {
      for (file <- file.listFiles()) {
        removeDir(file)
      }
    }
    file.delete()
  }

  /**
    * Deletes an input folder asynchronously.
    *
    * @param path from the folder that will be removed from the hard disk.
    * @return the asynchronous [[Future]], in case we want to do an action after
    *         the folder is removed from the hard disk drive or if we want
    *         to block the [[Thread]].
    */
  @inline
  def removeDirAsync(path: String): Future[Unit] = {
    Future(removeDir(new File(path)))
  }
}
