package scala.collection.immutable

import scala.collection.SetLike
import scala.collection.generic.{ImmutableSetFactory, CanBuildFrom, GenericSetTemplate, GenericCompanion}

object IndexedSeqSet extends ImmutableSetFactory[IndexedSeqSet] {
  implicit def canBuildFrom[A]: CanBuildFrom[Coll, A, IndexedSeqSet[A]] = setCanBuildFrom[A]
  override def empty[A]: IndexedSeqSet[A] = new IndexedSeqSet(IndexedSeq.empty, Set.empty)
}

@serializable @SerialVersionUID(0xAB16FC9E83F88EC7L) // scala.collection.immutable.IndexedSeqSet-0
class IndexedSeqSet[A] private (
    val seq: IndexedSeq[A],
    val set: Set[A]) extends Set[A]
                        with GenericSetTemplate[A, IndexedSeqSet]
                        with SetLike[A, IndexedSeqSet[A]]{
  override def companion: GenericCompanion[IndexedSeqSet] = IndexedSeqSet
  override def stringPrefix = "RetSet"

  override def size: Int = seq.size

  def contains(elem: A): Boolean =
    set.contains(elem)
  def + (elem: A): IndexedSeqSet[A] =
    if (contains(elem)) this
      else new IndexedSeqSet(seq :+ elem, set + elem)
  def - (elem: A): IndexedSeqSet[A] =
    if (!contains(elem)) this
      else new IndexedSeqSet(seq.filter(_!=elem), set - elem)

  def ++ (seq: IndexedSeq[A]): IndexedSeqSet[A] = {
   val (newSeq,newSet) = {
      val lhs = new scala.collection.mutable.LinkedHashSet ++ seq
      (if (lhs.size == seq.size) seq else lhs.toIndexedSeq, lhs.toSet)
    }
    new IndexedSeqSet(newSeq, newSet)
  }

  def ++ (set: Set[A]): IndexedSeqSet[A] =
    new IndexedSeqSet(IndexedSeq.empty ++ set, set)

  override def toSeq: Seq[A] = seq
  override def toIndexedSeq[B >: A]: IndexedSeq[B] = seq
  override def toSet[B >: A]: Set[B] = set.asInstanceOf[Set[B]]

  def iterator =
    seq.iterator
  override def foreach[U](f: A => U): Unit =
    seq.foreach(f)
}
