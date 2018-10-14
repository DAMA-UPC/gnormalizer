package babel.graph

/**
  * Set of aliases useful when working with nodes
  * across all the Babel platform.
  */
trait NodeAliases {

  /**
    * Type alias represents a non-normalized [[Node]].
    */
  type Node = String

  /**
    * Type alias representing the [[NodeMapping]] index within the module.
    */
  type NodeMapping = Long

}

/**
  * @see [[NodeAliases]]
  */
object NodeAliases extends NodeAliases
