package gnormalizer

import Vertex.Vertex

/**
  * Represents the connection between two [[Vertex]].
  */
case class Edge(source : Vertex, target : Vertex) extends Comparable[Edge] {

  /**
    * Overloads the default [[compareTo()]] method. During ordering the [[Edge]]
    * are ordered comparing both [[Edge.source]] unless both are equal, in this
    * case the comparision between the [[Edge.target]] is returned instead.
    */
  override def compareTo(other: Edge): Int = {
    source.compare(other.source) match {
      case 0 => target.compare(other.target)
      case x => x
    }
  }
}
