package babel.graph

/**
  * This trait should be imported by 'package objects' exposing
  * the Babel platform graph capabilities.
  */
private[babel] trait GraphImplicits extends VertexAliases with EdgeOrdering
