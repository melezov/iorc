package scala.collection
package immutable

import generic._

object VectorHashSet extends ImmutableSetFactory[VectorHashSet] {
  implicit def canBuildFrom[A]: CanBuildFrom[Coll, A, VectorHashSet[A]] = setCanBuildFrom[A]
  override def empty[A]: VectorHashSet[A] = VectorHashSet[A]( Vector.empty )
  def apply[A]( seq: IndexedSeq[A] ): VectorHashSet[A] = {
    new VectorHashSet( seq, HashSet.empty ++ seq )
  }
}

@serializable @SerialVersionUID(0x676487D2A09BC676L)
class VectorHashSet[A] private (
    val seq: IndexedSeq[A],
    val set: Set[A]) extends Set[A]
                        with GenericSetTemplate[A, VectorHashSet]
                        with SetLike[A, VectorHashSet[A]]{

  override def size: Int = seq.size

  def contains(elem: A): Boolean =
    set.contains(elem)
  def + (elem: A): this.type =
    if (contains(elem)) this
    else new VectorHashSet( seq :+ elem, set + elem )
  def - (elem: A): this.type =
    if ( !contains(elem)) this
    else new VectorHashSet( seq.filterNot(_==elem), set - elem )
  def iterator: Iterator[A] =
    seq.iterator
  override def foreach[U](f: A =>  U): Unit =
    seq.foreach( f )
}
