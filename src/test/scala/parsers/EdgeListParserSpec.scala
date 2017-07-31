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
class EdgeListParserSpec extends Specification with ScalaCheck {

  /**
    * Normalizes a ScalaCheck vertex string to do have any invalid parameter such as,
    * white spaces within a [[models.Edge]], '\n' characters or commented
    * lines.
    */
  private[this] def normalizeScalacheckVertexString(parser: EdgeListParser, vertex: String) = {
    val normalizedTestVertex = {
      vertex.trim
        .replaceAll(" ", "")
        .replaceAll("\n", "")
        .replaceAll("\r", "")
        .replaceAll("\u000b", "")
        .replaceAll("\u000c", "")
        .replaceAll("\u0009", "")
    }
    normalizedTestVertex match {
      case "" => "TestVertex"
      case _ if parser.commentedLinesStartCharacters.exists(normalizedTestVertex.startsWith) =>
        s"V$normalizedTestVertex"
      case _ => normalizedTestVertex
    }
  }

  private[this] def prepareScalaCheckTest(parser: EdgeListParser, a: String, b: String): String = {
    val normalizedA = normalizeScalacheckVertexString(parser, a)
    val normalizedB = normalizeScalacheckVertexString(parser, b)
    // In this test Vertex 'A' and 'B' cannot be equal
    if (normalizedA.equals(normalizedB)) {
      s"$normalizedA ${normalizedB}2"
    } else {
      s"$normalizedA $normalizedB"
    }
  }

  /**
    * Checks if the parser result is valid or not.
    */
  private[this] def checkResult(parser: EdgeListParser,
                                input: Stream[Task, String],
                                numberOfEdges: Int): MatchResult[_] = {
    parser.toEdgeStream(input).runLog.unsafeRun().size must beEqualTo(numberOfEdges)
  }

  "toStream() method" should {
    val parser: EdgeListParser = new EdgeListParser()
    "Must work when inputting a Stream with no elements" in {
      val emptyEdgeStream = Stream.eval(Task.now(""))
      checkResult(parser, emptyEdgeStream, 0)
    }
    "Must work when inputting with edges in numerical input edges" in {
      prop((a: Long, b: Long) => {
        parser.toEdgeStream(Stream.pure(s"$a $b")).runLog.unsafeRun().size must beEqualTo(1L)
      })
    }
    "Must work when inputting edges in non-numerical input edges" in {
      prop((sourceVertex: String, targetVertex: String) => {
        // Empty vertices are obviously not supported, so a 'V' prefix has being added.
        val inputEdge = prepareScalaCheckTest(parser, sourceVertex, targetVertex)
        val singleEdgeStream = Stream.eval(Task.now(inputEdge))
        checkResult(parser, singleEdgeStream, 1)
      })
    }
    parser.commentedLinesStartCharacters.foldLeft(Fragments()) { (acc, commentStart) =>
      acc.append(s"It must ignore edges starting with '$commentStart'" in {
        prop((inputVertex: String) => {
          val normalizedInputString = normalizeScalacheckVertexString(parser, inputVertex)
          val inputEdge = s"$commentStart$normalizedInputString"
          val singleEdgeStream = Stream.eval(Task.now(inputEdge))
          checkResult(parser, singleEdgeStream, 0)
        })
      })
    }
    "Must work when inputting two valid Strings divided by several whitespaces" in {
      prop((a: String, b: String, numberWhitespaces: Byte) => {
        val normalizedA = normalizeScalacheckVertexString(parser, a)
        val normalizedB = normalizeScalacheckVertexString(parser, b)
        val whitespaces: String = {
          " " * numberWhitespaces match {
            case "" | " " => "  "
            case severalWhitespaces => severalWhitespaces
          }
        }
        val inputEdge = s"$normalizedA$whitespaces$normalizedB"
        checkResult(parser, Stream.eval(Task.now(inputEdge)), 1)
      })
    }
    "When inputting multiple valid inputs, converts all of them to Edges" in {
      val numberOfInputEdges = 1000

      val expectation: Stream[Task, String] = {
        (0 until numberOfInputEdges)
          .map(i => s"$i $i")
          .map(a => Stream.eval(Task.now(a)))
          .foldLeft(Stream[Task, String]())(_ ++ _)
      }
      checkResult(parser, expectation, numberOfInputEdges)
    }
    "When inputting an invalid value, return a failed Stream" in {
      val invalidInputString: String = "a b c" // 3 vertices
      val invalidInput: Stream[Task, String] = Stream.eval(Task.now(invalidInputString))
      val stream: Stream[Task, Edge] = parser.toEdgeStream(invalidInput)
      stream.run.unsafeRun() must throwA[IllegalArgumentException]
    }
  }

  "mappingsStream() method" in {
    "Returns an empty mapping Stream when no elements were converted with a specific parser" in {
      val emptyParser: EdgeListParser = new EdgeListParser()
      emptyParser.mappingsStream() must beEmpty
    }
    "Returns the inserted Vertex mappings successfully when the Stream contains a single value" in {
      prop((a: String, b: String) => {
        // Generates the parser to test
        val testParser: EdgeListParser = new EdgeListParser()
        // Generate the test edges
        val testEdge = prepareScalaCheckTest(testParser, a, b)
        // Generates the parser input
        val input: Stream[Task, String] = Stream.eval(Task.now(testEdge))
        // Expectation
        testParser.toEdgeStream(input).run.unsafeRun()
        testParser.mappingsStream().size must beEqualTo(2)
      })
    }
    "Returns the inserted Vertex mappings when the Stream contains multiple values" in {
      // Generates the parser to test
      val parser: EdgeListParser = new EdgeListParser()
      // Input edges
      val firstEdge: String = s"TestEdgeSource TestEdgeTarget"
      val edges: Seq[String] = (1 until 1000).map(_.toString).map(index => s"$index ${index}A")
      // Parses the edges in the stream
      parser
        .toEdgeStream(edges.foldLeft(Stream.eval[Task, String](Task.now(firstEdge))) {
          (acc, edgeString) =>
            acc ++ Stream.eval(Task.now(edgeString))
        })
        .run
        .unsafeRun()
      // Number of edges * 2 (Source target)
      val expectedNumberEdges: Int = 2 * edges.size + 2
      // Expectation
      parser.mappingsStream().size must beEqualTo(expectedNumberEdges)
    }
  }
}
