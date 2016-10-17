package gnormalizer.sorters

import java.util.concurrent.atomic.AtomicLong

import gnormalizer.models.Edge

import scala.collection.mutable.{HashMap => MutableHashMap, TreeSet => MutableTreeSet}

import InMemorySorter.defaultMaxVertexesPerBucket

/**
  * Orders all the input [[Edge]] forcing not to use the
  * disk in the process, using the RAM memory instead.
  *
  * @param maxVerticesPerBucket the maximum amount of [[gnormalizer.models.Vertex]]
  *                             which adjacency's will be sorted in each internal bucket.
  */
final case class InMemorySorter(maxVerticesPerBucket: Int = defaultMaxVertexesPerBucket
                               ) extends Sorter {

  /**
    * In-memory buffered [[Edge]]s.
    */
  private[this] val inMemoryBuckets = MutableHashMap[Long, MutableTreeSet[Edge]]()

  /**
    * Counts the number of inserted [[Edge]]s.
    */
  private[this] val numberEdges: AtomicLong = new AtomicLong(0L)

  /**
    * @inheritdoc
    */
  override def addEdgeToResult(edge: Edge): Long = {
    val bucketId: Long = edge.source / maxVerticesPerBucket

    inMemoryBuckets.get(bucketId) match {
      case Some(bucket) =>
        bucket.synchronized {
          bucket += edge
        }
      case _ =>
        inMemoryBuckets.synchronized {
          val bucket = {
            inMemoryBuckets.
              getOrElseUpdate(
                key = bucketId,
                op = MutableTreeSet.empty(ordering = Edge.ordering)
              )
          }
          bucket.synchronized(bucket += edge)
        }
    }
    numberEdges.incrementAndGet()
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
  override def countNumberEdges(): Long = numberEdges.get()
}

/**
  * Companion object containing a set of default values
  * that any sorter can use @see [[Sorter]]
  */
object InMemorySorter {

  /**
    * The default amount of [[Edge]] which [[Edge.source]] node
    * are placed in a node Bucket at maximum.
    */
  @inline val defaultMaxVertexesPerBucket = 250
}
