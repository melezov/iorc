package scala.collection
package generic

import mutable.{Builder, SetBuilder}

abstract class RetSetFactory[CC[A] <: RetSet[A] with RetSetLike[A, CC[A]]] {
  type Coll = CC[_]

  def empty[A]: CC[A]

  def apply[A](elems: A*): CC[A] = (newBuilder[A] ++= elems).result

  def newBuilder[A]: Builder[A, CC[A]] = new SetBuilder[A, CC[A]](empty)

  implicit def retSetCanBuildFrom[A] = new CanBuildFrom[Coll, A, CC[A]] {
    def apply(from: Coll) = newBuilder[A]
    def apply() = newBuilder[A]
  }
}
