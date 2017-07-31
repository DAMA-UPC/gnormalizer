package gnormalizer.io

import fs2.{Task, Stream}

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
  def init(path: String): Stream[Task, String]
}
