package sorters

/**
  * Test @see [[InMemorySorter]]
  */
class InMemorySorterTest extends SorterTest {

  override def generateSorter(maxVerticesPerBucket : Int): InMemorySorter = {
    new InMemorySorter(maxVerticesPerBucket = maxVerticesPerBucket)
  }

  override def defaultNumberVertexesPerBucket: Int = InMemorySorter.defaultMaxVertexesPerBucket
}
