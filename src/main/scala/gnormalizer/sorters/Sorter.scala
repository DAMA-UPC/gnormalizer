package gnormalizer.sorters

import gnormalizer.models.Edge

import scala.collection.mutable.{HashMap => MutableHashMap, TreeSet => MutableTreeSet}

/**
  * Base Sorter for all graph sorters.
  */
trait Sorter {

  val maxBucketSize : Int

  /**
    * In-memory buffered [[Edge]]s.
    */
  protected final val inMemoryBuckets = MutableHashMap[Long, MutableTreeSet[Edge]]()

  /**
    * Counts the number of inserted [[Edge]]s.
    */
  private[this] var numberEdges: Long = 0

  /**
    * This method will be called when wanting to add an [[Edge]]
    * to be ordered.
    *
    * @param edge that will be added to the result.
    */
  def addEdgeToResult(edge: Edge): Unit = {
    val bucketId: Long = edge.source / maxBucketSize

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
    * Initializes a [[Stream]] of all the [[Edge]]'s added previously
    * calling the method [[Sorter.addEdgeToResult()]] sorted
    *
    * @return a [[Stream]] with the [[Edge]]'s ordered.
    */
  def resultStream(): Stream[Edge]

  /**
    * Obtains the number of buckets used during the sorting process.
    */
  def countNumberBuckets(): Int = inMemoryBuckets.size

  /**
    * Obtains the number of processed [[Edge]]s.
    */
  def countNumberEdges(): Long = numberEdges

  /**
    * Adds an [[Edge]] to a specific in-memory bucket.
    *
    * @param bucket where the [[Edge]] will be inserted.
    * @param edge   that will be inserted into the bucket.
    */
  @inline
  protected def addEdgeToBucket(bucket: MutableTreeSet[Edge], edge: Edge) = {
    bucket.synchronized {
      bucket += edge
      numberEdges += 1L
    }
  }
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
