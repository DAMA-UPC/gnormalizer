package parsers

import fs2.{Stream, Task}
import models.Edge
import org.specs2.ScalaCheck
import org.specs2.matcher.MatchResult
import org.specs2.mutable.Specification
import org.specs2.specification.core.Fragments

/**
  * Test for @see [[EdgeListParser]]
  */
@SuppressWarnings(Array("org.wartremover.warts.Any")) // Can't use ScalaCheck otherwise
class EdgeListParserTest extends Specification with ScalaCheck {

  /**
    * Normalizes a ScalaCheck vertex string to do have any invalid parameter such as,
    * white spaces within a [[models.Edge]], '\n' characters or commented
    * lines.
    */
  private[this] def normalizeScalacheckVertexString(vertex: String) = {
    val normalizedTestVertex = {
      vertex
        .trim
        .replaceAll(" ", "")
        .replaceAll("\n", "")
        .replaceAll("\r", "")
        .replaceAll("\u000c", "")
    }
    if (normalizedTestVertex.isEmpty) {
      "TestVertex"
    } else if (normalizedTestVertex.startsWith("#") || normalizedTestVertex.startsWith("//")) {
      s"V$normalizedTestVertex"
    } else {
      normalizedTestVertex
    }
  }

  /**
    * Checks if the parser result is valid or not.
    */
  private[this] def checkResult(input: Stream[Task, String],
                                numberOfEdges: Int): MatchResult[_] = {
    EdgeListParser.toEdgeStream(input).runLog.unsafeRun().size must beEqualTo(numberOfEdges)
  }

  "toStream() method" should {
    "Must work when inputting a Stream with no elements" in {
      val emptyEdgeStream = Stream.eval(Task.now(""))
      checkResult(emptyEdgeStream, 0)
    }
    "Must work when inputting with edges in numerical input edges" in {
      prop(
        (a: Long, b: Long) => {
          val singleEdgeStream = Stream.eval(Task.now(s"$a $b"))
          checkResult(singleEdgeStream, 1)
        }
      )
    }
    "Must work when inputting edges in non-numerical input edges" in {
      def prepareScalaCheckTest(a: String, b: String): String = {
        val normalizedA = normalizeScalacheckVertexString(a)
        val normalizedB = normalizeScalacheckVertexString(b)
        // In this test Vertex 'A' and 'B' cannot be equal
        if (normalizedA.equals(normalizedB)) {
          s"$normalizedA ${normalizedB}2"
        } else {
          s"$normalizedA $normalizedB"
        }
      }
      prop(
        (sourceVertex: String, targetVertex: String) => {
          // Empty vertices are obviously not supported, so a 'V' prefix has being added.
          val inputEdge = prepareScalaCheckTest(sourceVertex, targetVertex)
          val singleEdgeStream = Stream.eval(Task.now(inputEdge))
          checkResult(singleEdgeStream, 1)
        }
      )
    }
    EdgeListParser.commentedLinesStartCharacters.foldLeft(Fragments()) {
      (acc, commentStart) =>
        acc.append(
          s"It must ignore edges starting with '$commentStart'" in {
            prop(
              (inputVertex: String) => {
                val normalizedInputString = normalizeScalacheckVertexString(inputVertex)
                // Empty vertices are obviously not supported, so a 'V' prefix has being added.
                val inputEdge = s"$commentStart$normalizedInputString"
                val singleEdgeStream = Stream.eval(Task.now(inputEdge))
                checkResult(singleEdgeStream, 0)
              }
            )
          }
        )
    }
    "When inputting two " in {
      prop(
        (a : String, b : String) => {
          val normalizedA = normalizeScalacheckVertexString(a)
          val normalizedB = normalizeScalacheckVertexString(b)
          val inputEdge = s"$normalizedA      $normalizedB"
          checkResult(Stream.eval(Task.now(inputEdge)), 1)
        }
      )
    }
    "When inputting multiple valid inputs, converts all of them to Edges" in {
      val numberOfInputEdges = 1000

      val expectation: Stream[Task, String] = {
        (0 until numberOfInputEdges)
          .map(i => s"$i $i")
          .map(a => Stream.eval(Task.now(a)))
          .reduce(_ ++ _)
      }
      checkResult(expectation, numberOfInputEdges)
    }
  }
}
