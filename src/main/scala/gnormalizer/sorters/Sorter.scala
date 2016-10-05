package gnormalizer.sorters

import gnormalizer.models.Edge

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

  /**
    * Obtains the number of buckets used during the class testings.
    * Method really useful for testing porpoises.
    */
  def countNumberBuckets(): Int
}

/**
  * Companion object containing a set of default values
  * that any sorter can use @see [[Sorter]]
  */
object Sorter {

  /**
    * The default amount of [[Edge]] which [[Edge.source]] node
    * are placed in a node Bucket at maximum.
    */
  @inline val defaultMaxBucketSize = 500
}
