package gnormalizer.sorters

import gnormalizer.Edge

import scala.collection.mutable

/**
  * Orders all the input [[Edge]] forcing not to use the disk in the process,
  * forcing the memory usage during all the process.
  *
  * @param resultTreeNodeBucketKeySize the maximum amount of key values in each bucket.
  *                                    The key values corresponds to the [[Edge.source]]
  */
final case class InMemoryEdgeSorter(resultTreeNodeBucketKeySize : Int =
                                    InMemoryEdgeSorter.defaultSourceNodeMaxBucketSize
                                   ) extends Sorter {

  private[this] val resultBuckets = mutable.LongMap[mutable.TreeSet[Edge]]()

  /**
    * Adds an [[Edge]] to order to the result.
    *
    * @param edge to be ordered.
    */
  def addEdgeToResult(edge: Edge): Unit = {
    val bucketId: Long = edge.source / resultTreeNodeBucketKeySize

    resultBuckets.get(bucketId) match {
      case Some(bucket) =>
        bucket.synchronized {
          bucket += edge
        }
      case _ =>
        resultBuckets.synchronized {
          resultBuckets.get(bucketId) match {
            case Some(bucket) =>
              bucket.synchronized {
                bucket += edge
              }
            case _ =>
              val orderedBucketTree = mutable.TreeSet.empty(ordering = Edge.ordering)
              orderedBucketTree += edge
              resultBuckets += (bucketId -> orderedBucketTree)
          }
        }
    }
  }

  /**
    * @inheritdoc
    */
  def resultStream(): Stream[Edge] = {
    resultBuckets
      .keys
      .toList
      .sorted
      .map(resultBuckets.get)
      .collect { case Some(key) => key }
      .map(_.toStream)
      .foldLeft(Stream[Edge]())(_ #::: _)
  }

  /**
    * @return the number of buckets used for storing the sorting result.
    */
  private[sorters] def numberBuckets: Int = resultBuckets.size

}

/**
  * Companion object for @see [[InMemoryEdgeSorter]]
  */
object InMemoryEdgeSorter {

  /**
    * The default amount of [[Edge]] which [[Edge.source]] node
    * are placed in a node Bucket at maximum.
    */
  @inline val defaultSourceNodeMaxBucketSize : Int = 500
}
