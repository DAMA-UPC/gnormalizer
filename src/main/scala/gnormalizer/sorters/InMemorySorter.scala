package gnormalizer.sorters

import gnormalizer.models.Edge

import scala.collection.mutable.{HashMap => MutableHashMap, TreeSet => MutableTreeSet}

/**
  * Orders all the input [[Edge]] forcing not to use the
  * disk in the process, using the RAM memory instead.
  *
  * @param maxVerticesPerBucket the maximum amount of [[gnormalizer.models.Vertex]]
  *                             which adjacency's will be sorted in each internal bucket.
  */
final case class InMemorySorter(maxVerticesPerBucket: Int = Sorter.defaultMaxVerticesPerBucket
                               ) extends Sorter {

  /**
    * In-memory buffered [[Edge]]s.
    */
  private[this] val inMemoryBuckets = MutableHashMap[Long, MutableTreeSet[Edge]]()

  /**
    * Counts the number of inserted [[Edge]]s.
    */
  private[this] var numberEdges: Long = 0

  /**
    * @inheritdoc
    */
  override def addEdgeToResult(edge: Edge): Unit = {
    val bucketId: Long = edge.source / maxVerticesPerBucket

    inMemoryBuckets.get(bucketId) match {
      case Some(bucket) =>
        addEdgeToBucket(bucket, edge)
      case _ =>
        inMemoryBuckets.synchronized {
          inMemoryBuckets.get(bucketId) match {
            case Some(bucket) =>
              addEdgeToBucket(bucket, edge)
            case _ =>
              val orderedBucketTree = MutableTreeSet.empty(ordering = Edge.ordering)
              addEdgeToBucket(orderedBucketTree, edge)
              inMemoryBuckets += (bucketId -> orderedBucketTree)
          }
        }
    }
  }

  /**
    * @inheritdoc
    */
  def resultStream(): Stream[Edge] = {
    inMemoryBuckets
      .keys
      .toList
      .sorted
      .map(inMemoryBuckets.get)
      .collect { case Some(key) => key }
      .map(_.toStream)
      .foldLeft(Stream[Edge]())(_ #::: _)
  }

  /**
    * Adds an [[Edge]] to a specific in-memory bucket.
    *
    * @param bucket where the [[Edge]] will be inserted.
    * @param edge   that will be inserted into the bucket.
    */
  @inline
  private[this] def addEdgeToBucket(bucket: MutableTreeSet[Edge], edge: Edge) = {
    bucket.synchronized {
      bucket += edge
      numberEdges += 1L
    }
  }

  /**
    * @inheritdoc
    */
  override def countNumberBuckets(): Int = inMemoryBuckets.size

  /**
    * @inheritdoc
    */
  override def countNumberEdges(): Long = numberEdges
}
