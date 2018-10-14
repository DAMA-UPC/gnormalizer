package gnormalizer.sorters

/**
  * Test @see [[InMemorySorter]]
  */
class InMemorySorterSpec extends SorterSpec {

  override def generateSorter(maxNodesPerBucket: Int): InMemorySorter = {
    new InMemorySorter(maxNodesPErBucket = maxNodesPerBucket)
  }

  override def defaultNumberNodesPerBucket: Int = InMemorySorter.defaultMaxNodesPerBucket
}
