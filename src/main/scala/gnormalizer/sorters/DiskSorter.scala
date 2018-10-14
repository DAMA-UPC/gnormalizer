package gnormalizer.sorters

import java.util.UUID
import java.util.concurrent.atomic.AtomicLong

import babel.graph.Edge
import better.files._

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.{HashMap => MutableHashMap, TreeSet => MutableTreeSet}

/**
  * Orders all the input [[Edge]] forcing not to use the disk in the process,
  * forcing the memory usage during all the process.
  *
  * @param maxNodesPerBucket the maximum amount of key values in each bucket.
  *                      The key values corresponds to the [[Edge.source]]
  */
final class DiskSorter(maxNodesPerBucket: Int = DiskSorter.defaultMaxNodesPerBucket,
                       maxEdgesPerBucket: Int = DiskSorter.defaultMaxEdgesPerBucket,
                       temporalFileLocation: String = DiskSorter.defaultTemporalFilePathPrefix)
    extends Sorter {

  private[this] val bufferedEdges = MutableHashMap[Long, mutable.MutableList[Edge]]()
  private[this] val filePaths = MutableHashMap[Long, String]()

  /**
    * Counts the number of inserted [[Edge]]s.
    */
  private[this] val numberEdges: AtomicLong = new AtomicLong(0L)

  /**
    * @inheritdoc
    */
  @tailrec
  override def addEdgeToResult(edge: Edge): Long = {

    val bucketId: Long = edge.source / maxNodesPerBucket

    bufferedEdges.get(bucketId) match {
      case Some(bucket) =>
        bucket.synchronized {
          bucket += edge
          if (bucket.size > maxEdgesPerBucket) {
            // Stores the buffered edges in a file
            filePaths
              .getOrElseUpdate(bucketId, s"$temporalFileLocation${UUID.randomUUID()}")
              .toFile
              .createIfNotExists(createParents = true)
              .appendLine(bucket.map(_.toString).mkString("\n"))
            bucket.clear()
          }
        }
        numberEdges.incrementAndGet()
      case _ =>
        bufferedEdges.synchronized(bufferedEdges.getOrElseUpdate(bucketId, mutable.MutableList()))
        addEdgeToResult(edge)
    }
  }

  /**
    * @inheritdoc
    */
  override def resultStream(): Stream[Edge] = {
    bufferedEdges.keys.toList.sorted
      .map(key => (bufferedEdges.get(key), filePaths.get(key)))
      .collect { case (Some(key), filePath) => (key, filePath) }
      .collect {
        case (buffer, Some(diskBucket)) =>
          () =>
            {
              // There some stored edges on the HDD
              (MutableTreeSet.empty(Edge.edgeOrdering) ++ buffer ++ {
                diskBucket.toFile.lines.toSeq
                  .map(_.split(' '))
                  .collect {
                    case Array(source, target) =>
                      Edge(source.toLong, target.toLong)
                  }
              }).toStream
            }
        case (buffer, _) =>
          () =>
            // There no stored edges on the HDD
            (MutableTreeSet.empty(Edge.edgeOrdering) ++ buffer).toStream
      }
      .foldLeft(Stream[Edge]())((acc, f) => acc #::: f())
  }

  /**
    * Returns the used temporal files where the [[Edge]]'s are allocated.
    *
    * Note: This will not return all the buckets, it will return the buckets
    * which size exceed [[DiskSorter.maxEdgesPerBucket]], so its elements where
    * stored within a file in order to avoid wasting too much memory.
    */
  private[sorters] def usedFilePaths(): Seq[String] = filePaths.values.toSeq

  /**
    * @inheritdoc
    */
  override def countNumberBuckets(): Int = bufferedEdges.size

  /**
    * @inheritdoc
    */
  override def countNumberEdges(): Long = numberEdges.get()
}

/**
  * Companion object containing a set of default values
  * that any sorter can use @see [[Sorter]]
  */
object DiskSorter {

  /**
    * Sets the maximum amount of different [[Edge.source]]
    * that can fit in each internal [[DiskSorter]] bucket.
    */
  @inline val defaultMaxNodesPerBucket: Int = 1000

  /**
    * Default max number of [[Edge]]s per buffered bucket before
    * starting to store that edge contents into disk.
    */
  @inline val defaultMaxEdgesPerBucket: Int = 10000

  /**
    * The location where the temporal stored files are located.
    */
  @inline val defaultTemporalFilePathPrefix: String = "target/temp/"

}
