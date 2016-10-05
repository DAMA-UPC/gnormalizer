package gnormalizer.sorters

import gnormalizer.models.Edge

import scala.collection.mutable

/**
  * Orders all the input [[Edge]] forcing not to use the
  * disk in the process, using the RAM memory instead.
  *
  * @param maxBucketSize the maximum amount of [[gnormalizer.models.Vertex]] which adjacency's
  *                      will be sorted in each internal bucket.
  */
final case class InMemorySorter(maxBucketSize: Int = Sorter.defaultMaxBucketSize) extends Sorter {

  private[this] val resultBuckets = mutable.LongMap[mutable.TreeSet[Edge]]()

  /**
    * @inheritdoc
    */
  def addEdgeToResult(edge: Edge): Unit = {
    val bucketId: Long = edge.source / maxBucketSize

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
    * @inheritdoc
    */
  override def countNumberBuckets(): Int = resultBuckets.size

}
