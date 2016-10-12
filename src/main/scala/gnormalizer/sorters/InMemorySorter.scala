package gnormalizer.sorters

import gnormalizer.models.Edge

import scala.annotation.tailrec
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
  @tailrec
  override def addEdgeToResult(edge: Edge): Unit = {
    val bucketId: Long = edge.source / maxVerticesPerBucket

    inMemoryBuckets.get(bucketId) match {
      case Some(bucket) =>
        bucket.synchronized {
          bucket += edge
          numberEdges += 1L
        }
      case _ =>
        inMemoryBuckets.synchronized {
          if (!inMemoryBuckets.contains(bucketId)) {
            inMemoryBuckets += (bucketId -> MutableTreeSet.empty(ordering = Edge.ordering))
          }
        }
        addEdgeToResult(edge)
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
    * @inheritdoc
    */
  override def countNumberBuckets(): Int = inMemoryBuckets.size

  /**
    * @inheritdoc
    */
  override def countNumberEdges(): Long = numberEdges
}
