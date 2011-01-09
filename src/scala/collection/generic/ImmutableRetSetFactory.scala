package scala.collection
package generic

abstract class ImmutableRetSetFactory[CC[A] <: immutable.RetSet[A] with RetSetLike[A, CC[A]]] extends RetSetFactory[CC]
