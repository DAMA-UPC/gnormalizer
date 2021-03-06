package babel.graph

/**
  * Represents the connection between two [[NodeAliases]].
  */
case class Edge(source: NodeMapping, target: NodeMapping) extends Comparable[Edge] {

  /**
    * Overloads the default [[compareTo()]] method. During ordering the [[Edge]]
    * are ordered comparing both [[Edge.source]] unless both are equal, in this
    * case the comparision between the [[Edge.target]] is returned instead.
    */
  @inline
  @SuppressWarnings(Array("org.wartremover.warts.Equals")) // Performance reasons
  override def compareTo(other: Edge): Int = {
    val sourceComparision: Int = source.compareTo(other.source)
    // The comparision will always be type-safe.
    if (sourceComparision == 0) {
      this.target.compareTo(other.target)
    } else {
      sourceComparision
    }
  }

  /**
    * Overrides the default [[toString()]] implementation, so only the source and
    * the target are printed, so it's following the format from an 'Edge List graph'.
    *
    * For example:
    * '1 2'
    */
  @inline
  override def toString: String = s"$source $target"
}

/**
  * Companion object for [[Edge]].
  */
object Edge extends EdgeOrdering

trait EdgeOrdering {

  /**
    * Implicit ordering for [[Edge]]. Internally calls [[Edge.compareTo()]].
    */
  implicit val edgeOrdering: Ordering[Edge] = (p1: Edge, p2: Edge) => p1.compareTo(p2)
}