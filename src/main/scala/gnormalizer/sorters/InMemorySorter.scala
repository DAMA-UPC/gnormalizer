package gnormalizer.sorters

import gnormalizer.models.Edge

/**
  * Orders all the input [[Edge]] forcing not to use the
  * disk in the process, using the RAM memory instead.
  *
  * @param maxBucketSize the maximum amount of [[gnormalizer.models.Vertex]] which adjacency's
  *                      will be sorted in each internal bucket.
  */
final case class InMemorySorter(maxBucketSize: Int = Sorter.defaultMaxBucketSize) extends Sorter {

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
}
