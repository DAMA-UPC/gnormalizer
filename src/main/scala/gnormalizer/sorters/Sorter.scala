package gnormalizer.sorters

import gnormalizer.Edge

/**
  * Base trait for all graph sorters.
  */
trait Sorter {

  /**
    * This method will be called when wanting to add an [[Edge]]
    * to be ordered.
    *
    * @param edge that will be added to the result.
    */
  def addEdgeToResult(edge: Edge): Unit

  /**
    * Initializes a [[Stream]] of all the [[Edge]]'s added previously
    * calling the method [[Sorter.addEdgeToResult()]] sorted
    *
    * @return a [[Stream]] with the [[Edge]]'s ordered.
    */
  def resultStream(): Stream[Edge]
}
