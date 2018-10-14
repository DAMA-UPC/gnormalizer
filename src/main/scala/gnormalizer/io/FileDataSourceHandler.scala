package gnormalizer.io

import java.nio.file.Paths
import java.util.concurrent.Executors

import cats.effect.{ContextShift, IO}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService}

/**
  * Object containing a set of methods used for managing file [[DataSourceHandler]].
  */
class FileDataSourceHandler extends DataSourceHandler {

  /**
    * @inheritdoc
    */
  @SuppressWarnings(
    Array("org.wartremover.warts.Nothing",
          "org.wartremover.warts.Any",
          "org.wartremover.warts.ImplicitParameter")
  )
  override def init(path: String)(
    implicit ec: ExecutionContext = FileDataSourceHandler.defaultExecutionContext,
    cs: ContextShift[IO] = FileDataSourceHandler.defaultContextShift
  ): fs2.Stream[cats.effect.IO, String] =
    fs2.io.file
      .readAll[IO](Paths.get(path), ec, FileDataSourceHandler.chunkSize)
      .through(fs2.text.utf8Decode)
      .through(fs2.text.lines)
}

object FileDataSourceHandler {

  /**
    * Maximum size of the data chunks being
    */
  @inline val chunkSize: Int = 4096

  private def defaultExecutionContext: ExecutionContextExecutorService =
    ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(8))

  private def defaultContextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
}
