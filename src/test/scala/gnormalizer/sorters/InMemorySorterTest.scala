package gnormalizer.sorters

/**
  * Test @see [[InMemorySorter]]
  */
class InMemorySorterTest extends SorterTest {

  override def generateSorter(numberBuckets : Int): InMemorySorter = InMemorySorter(numberBuckets)

  override val numParallelVertex: Int = 10000
}
