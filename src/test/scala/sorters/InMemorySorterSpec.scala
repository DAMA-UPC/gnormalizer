package sorters

/**
  * Test @see [[InMemorySorter]]
  */
class InMemorySorterSpec extends SorterSpec {

  override def generateSorter(maxVerticesPerBucket: Int): InMemorySorter = {
    new InMemorySorter(maxVerticesPerBucket = maxVerticesPerBucket)
  }

  override def defaultNumberVertexesPerBucket: Int = InMemorySorter.defaultMaxVertexesPerBucket
}
