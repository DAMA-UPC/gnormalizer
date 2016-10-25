package sorters

import models.Edge

/**
  * Base Sorter for all graph sorters.
  */
trait Sorter {

  /**
    * This method will be called when wanting to add an [[Edge]] to be ordered.
    *
    * @param edge that will be added to the result.
    * @return the number of [[Edge]]s that has being already inserted.
    */
  def addEdgeToResult(edge: Edge): Long

  /**
    * Initializes a [[Stream]] of all the [[Edge]]'s added previously
    * calling the method [[Sorter.addEdgeToResult()]] sorted
    *
    * @return a [[Stream]] with the [[Edge]]'s ordered.
    */
  def resultStream(): Stream[Edge]

  /**
    * Obtains the number of buckets used during the sorting process.
    */
  def countNumberBuckets(): Int

  /**
    * Obtains the number of processed [[Edge]]s.
    */
  def countNumberEdges(): Long

}
