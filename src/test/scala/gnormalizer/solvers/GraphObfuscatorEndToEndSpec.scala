package gnormalizer.solvers

import org.specs2.mutable.Specification
import babel.graph.edgeOrdering

/**
  * End to end tests for [[GraphObfuscator]].
  */
class GraphObfuscatorEndToEndSpec extends Specification {

  private[this] val testFile = "./src/test/resources/edge_list/test_edge_list_to_obfuscate"

  private[this] val numberDistinctVertexInFile: Int = 5

  private[this] val numberEdgesInFile: Int = 4

  "GraphObfuscator" should {
    "Obfuscate all input graph elements and put them on order" in {
      val result = GraphObfuscator.obfuscateEdgeListGraph(testFile).unsafeRunSync().toList

      (0 until numberDistinctVertexInFile)
        .map(v => result.exists(e => e.source == v || e.target == v) must beTrue)

      result.size must beEqualTo(numberEdgesInFile)
      result must beEqualTo(result.sorted)
    }
  }
}
