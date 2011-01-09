package scala.collection
package immutable

import generic._

object VectorHashSet extends ImmutableSetFactory[VectorHashSet] {
  implicit def canBuildFrom[A]: CanBuildFrom[Coll, A, VectorHashSet[A]] = setCanBuildFrom[A]
  override def empty[A]: VectorHashSet[A] =
    new VectorHashSet[A]( Vector.empty, Set.empty )
  def apply[A]( seq: IndexedSeq[A] ): VectorHashSet[A] =
    new VectorHashSet( seq, Set.empty ++ seq )
  def apply[A]( set: Set[A] ): VectorHashSet[A] =
    new VectorHashSet( Vector.empty ++ set, set )
}

@serializable @SerialVersionUID(0x676487D2A09BC676L)
class VectorHashSet[A] private (
    val seq: IndexedSeq[A],
    val set: Set[A] ) extends Set[A]
                        with GenericSetTemplate[A, VectorHashSet]
                        with SetLike[A, VectorHashSet[A]]{
  override def companion: GenericCompanion[VectorHashSet] = VectorHashSet
  override def stringPrefix = "RetSet"

  override def size: Int = seq.size

  def contains(elem: A): Boolean =
    set.contains(elem)
  def + (elem: A): VectorHashSet[A] =
    if (contains(elem)) this
    else new VectorHashSet( seq :+ elem, set + elem )
  def - (elem: A): VectorHashSet[A] =
    if ( !contains(elem)) this
    else new VectorHashSet( seq.filter(_!=elem), set - elem )

  def iterator =
    seq.iterator
  override def foreach[U](f: A => U): Unit =
    seq.foreach( f )
}
