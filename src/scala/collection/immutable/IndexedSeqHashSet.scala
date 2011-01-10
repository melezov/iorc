package scala.collection
package immutable

import generic._

object IndexedSeqHashSet extends ImmutableSetFactory[IndexedSeqHashSet] {
  implicit def canBuildFrom[A]: CanBuildFrom[Coll, A, IndexedSeqHashSet[A]] = setCanBuildFrom[A]
  override def empty[A]: IndexedSeqHashSet[A] =
    new IndexedSeqHashSet( IndexedSeq.empty, Set.empty )
  def apply[A]( seq: IndexedSeq[A] ): IndexedSeqHashSet[A] =
    new IndexedSeqHashSet( seq, Set.empty ++ seq )
  def apply[A]( set: Set[A] ): IndexedSeqHashSet[A] =
    new IndexedSeqHashSet( IndexedSeq.empty ++ set, set )
}

@serializable @SerialVersionUID(0x676487D2A09BC676L)
class IndexedSeqHashSet[A] private (
    val seq: IndexedSeq[A],
    val set: Set[A] ) extends Set[A]
                        with GenericSetTemplate[A, IndexedSeqHashSet]
                        with SetLike[A, IndexedSeqHashSet[A]]{
  override def companion: GenericCompanion[IndexedSeqHashSet] = IndexedSeqHashSet
  override def stringPrefix = "RetSet"

  override def size: Int = seq.size

  def contains(elem: A): Boolean =
    set.contains(elem)
  def + (elem: A): IndexedSeqHashSet[A] =
    if (contains(elem)) this
    else new IndexedSeqHashSet( seq :+ elem, set + elem )
  def - (elem: A): IndexedSeqHashSet[A] =
    if ( !contains(elem)) this
    else new IndexedSeqHashSet( seq.filter(_!=elem), set - elem )

  override def toSeq: Seq[A] = seq
  override def toIndexedSeq[B >: A]: immutable.IndexedSeq[B] = seq

  def iterator =
    seq.iterator
  override def foreach[U](f: A => U): Unit =
    seq.foreach( f )
}
