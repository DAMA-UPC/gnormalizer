package babel.graph

import java.security.SecureRandom

import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

/**
  * Test for: @see [[Edge]]
  */
class EdgeSpec extends Specification with ScalaCheck {

  "compareTo method" should {
    def any: NodeMapping = new SecureRandom().nextInt()
    "Return '0' when two edges are equal" in {
      val sourceAndTargetEdge = Edge(source = any, target = any)
      sourceAndTargetEdge.compareTo(sourceAndTargetEdge) must beEqualTo(0)
    }
    "Return a positive number when the input edge has a lower source Node" in {
      val sourceEdge: Edge = Edge(source = 2, target = any)
      val targetEdge: Edge = Edge(source = 1, target = any)
      sourceEdge.compareTo(targetEdge) must beGreaterThan(0)
    }
    "Return a negative number when the input edge has a greater source Node" in {
      val sourceEdge: Edge = Edge(source = 1, target = any)
      val targetEdge: Edge = Edge(source = 2, target = any)
      sourceEdge.compareTo(targetEdge) must beLessThan(0)
    }
    "Return a positive number when the input edge has same source and lower target" in {
      val sourceEdge: Edge = Edge(source = any, target = 2)
      val targetEdge: Edge = sourceEdge.copy(target = 1)
      sourceEdge.compareTo(targetEdge) must beGreaterThan(0)
    }
    "Return a negative number when the input edge has same source and greater target" in {
      val sourceEdge: Edge = Edge(source = any, target = 1)
      val targetEdge: Edge = sourceEdge.copy(target = 2)
      sourceEdge.compareTo(targetEdge) must beLessThan(0)
    }
  }

  "toString() method" should {
    "Return the first element appended to the second element separated by a whitespace" >> {
      val numberScalaCheckTries: Int = 200
      val numberWorkers: Int = 4
      prop((a: NodeMapping, b: NodeMapping) => Edge(a, b).toString.equals(s"$a $b"))
        .set(minTestsOk = numberScalaCheckTries, workers = numberWorkers)
    }
  }
}
