package gnormalizer.api
import babel.graph.GraphFormat

/**
 * Contains a list with the accepted graph types for the input and outputs.
 */
trait AcceptedGraphFormats {
  case object EdgeList extends GraphFormat
}

/**
 * @see [[AcceptedGraphFormats]]
 */
object AcceptedGraphFormats extends AcceptedGraphFormats
