package gnormalizer.io

import cats.effect.{IO, ContextShift}
import scala.concurrent.ExecutionContext

/**
  * Object containing a set of methods used for initializing file [[Stream]]s.
  */
trait DataSourceHandler {

  /**
    * Initializes a [[Stream]] on the given source.
    *
    * @param path where the [[Stream]] will be initialized.
    * @return a [[Stream]] that will be return the graph contents as UTF-8 [[String]]'s.
    */

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  def init(path: String)(implicit ec: ExecutionContext,
                         cs: ContextShift[IO]): fs2.Stream[cats.effect.IO, String]
}
