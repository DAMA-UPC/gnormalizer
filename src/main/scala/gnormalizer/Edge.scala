package gnormalizer

import gnormalizer.Vertex.Vertex

/**
  * Represents the connection between two [[Vertex]].
  */
case class Edge(source: Vertex, target: Vertex) extends Comparable[Edge] {

  /**
    * Overloads the default [[compareTo()]] method. During ordering the [[Edge]]
    * are ordered comparing both [[Edge.source]] unless both are equal, in this
    * case the comparision between the [[Edge.target]] is returned instead.
    */
  override def compareTo(other: Edge): Int = {
    val sourceComparision: Int = source.compareTo(other.source)
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
  override def toString: String = s"$source $target"
}
