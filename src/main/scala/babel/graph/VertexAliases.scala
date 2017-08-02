package babel.graph

/**
  * Set of aliases useful when working with vertices
  * across all the Babel platform.
  */
trait VertexAliases {

  /**
    * Type alias represents a non-normalized [[Vertex]].
    */
  type Vertex = String

  /**
    * Type alias representing the [[Vertex]] index within the module.
    */
  type VertexMapping = Long
}

/**
  * @see [[VertexAliases]]
  */
object VertexAliases extends VertexAliases
