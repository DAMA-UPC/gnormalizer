package gnormalizer

import babel.graph.GraphFormat
import gnormalizer.api.{AcceptedGraphFormats, GnormalizerApiBuilder}
import org.specs2.mutable.Specification

/**
  * Specification tests for [[gnormalizer]].
  */
class gnormalizerSpec extends Specification {

  "Gnormalizer package" should {

    "Contain the accepted Graph formats" should {
      "In the base package" in {
        gnormalizer.EdgeList must beAnInstanceOf[GraphFormat]
      }
      "In the internal 'Gnormalizer' object" in {
        Gnormalizer.GraphFormats must beAnInstanceOf[AcceptedGraphFormats]
      }
    }

    "builder() method creates a Gnormalizer API builder" in {
      Gnormalizer.builder() must beAnInstanceOf[GnormalizerApiBuilder]
    }
  }
}
