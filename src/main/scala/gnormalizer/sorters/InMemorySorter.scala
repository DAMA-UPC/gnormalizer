package gnormalizer.sorters

import java.util.concurrent.atomic.AtomicLong

import babel.graph.{Edge, Node}
import gnormalizer.sorters.InMemorySorter.defaultMaxNodesPerBucket

import scala.collection.mutable.{HashMap => MutableHashMap, TreeSet => MutableTreeSet}

/**
  * Orders all the input [[Edge]] forcing not to use the
  * disk in the process, using the RAM memory instead.
  *
  * @param maxNodesPErBucket the maximum amount of [[Node]]
  *                             which adjacency's will be sorted in each internal bucket.
  */
final class InMemorySorter(maxNodesPErBucket: Int = defaultMaxNodesPerBucket) extends Sorter {

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
    val bucketId: Long = edge.source / maxNodesPErBucket

    inMemoryBuckets.get(bucketId) match {
      case Some(bucket) =>
        bucket.synchronized(bucket += edge)
      case _ =>
        inMemoryBuckets.synchronized {
          val bucket = {
            inMemoryBuckets.getOrElseUpdate(key = bucketId,
                                            defaultValue =
                                              MutableTreeSet.empty(ordering = Edge.edgeOrdering))
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
    inMemoryBuckets.keys.toList.sorted
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
  @inline val defaultMaxNodesPerBucket: Int = 250
}
