package gnormalizer

import java.security.SecureRandom

import gnormalizer.Vertex.Vertex
import org.specs2.mutable.Specification

/**
  * Test for: @see [[Edge]]
  */
class EdgeTest extends Specification {

  "compareTo method" should {
    def any: Vertex = new SecureRandom().nextLong()
    "Return '0' when two edges are equal" in {
      val sourceAndTargetEdge = Edge(source = any, target = any)
      sourceAndTargetEdge.compareTo(sourceAndTargetEdge) must beEqualTo(0)
    }
    "Return a positive number when the input edge has a lower source Vertex" in {
      val sourceEdge: Edge = Edge(source = 2L, target = any)
      val targetEdge: Edge = Edge(source = 1L, target = any)
      sourceEdge.compareTo(targetEdge) must beGreaterThan(0)
    }
    "Return a negative number when the input edge has a greater source Vertex" in {
      val sourceEdge: Edge = Edge(source = 1L, target = any)
      val targetEdge: Edge = Edge(source = 2L, target = any)
      sourceEdge.compareTo(targetEdge) must beLessThan(0)
    }
    "Return a positive number when the input edge has same source and lower target" in {
      val sourceEdge: Edge = Edge(source = any, target = 2L)
      val targetEdge: Edge = sourceEdge.copy(target = 1L)
      sourceEdge.compareTo(targetEdge) must beGreaterThan(0)
    }
    "Return a negative number when the input edge has same source and greater target" in {
      val sourceEdge: Edge = Edge(source = any, target = 1L)
      val targetEdge: Edge = sourceEdge.copy(target = 2L)
      sourceEdge.compareTo(targetEdge) must beLessThan(0)
    }
  }
}
