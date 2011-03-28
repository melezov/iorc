package scala.collection.immutable

import scala.collection.SetLike
import scala.collection.generic.{ImmutableSetFactory, CanBuildFrom, GenericSetTemplate, GenericCompanion}

object IndexedSeqSet extends ImmutableSetFactory[IndexedSeqSet] {
  implicit def canBuildFrom[A]: CanBuildFrom[Coll, A, IndexedSeqSet[A]] = setCanBuildFrom[A]
  override def empty[A]: IndexedSeqSet[A] = new IndexedSeqSet(IndexedSeq.empty, Set.empty)
}

@serializable @SerialVersionUID(0xAB16FC9E83F88EC7L) // scala.collection.immutable.IndexedSeqSet-0
class IndexedSeqSet[A] private (
    val s: IndexedSeq[A],
    val t: Set[A]) extends Set[A]
                        with GenericSetTemplate[A, IndexedSeqSet]
                        with SetLike[A, IndexedSeqSet[A]]{
//                        with Serializable{
  override def companion: GenericCompanion[IndexedSeqSet] = IndexedSeqSet
  override def stringPrefix = "RetSet"

  override def size: Int = s.size

  def contains(elem: A): Boolean =
    t.contains(elem)
  def + (elem: A): IndexedSeqSet[A] =
    if (contains(elem)) this
      else new IndexedSeqSet(s :+ elem, t + elem)
  def - (elem: A): IndexedSeqSet[A] =
    if (!contains(elem)) this
      else new IndexedSeqSet(s.filter(_!=elem), t - elem)

  def ++ (s: IndexedSeq[A]): IndexedSeqSet[A] = {
   val (newSeq,newSet) = {
      val lhs = new scala.collection.mutable.LinkedHashSet ++ s
      (if (lhs.size == s.size) s else lhs.toIndexedSeq, lhs.toSet)
    }
    new IndexedSeqSet(newSeq, newSet)
  }

  def ++ (t: Set[A]): IndexedSeqSet[A] =
    new IndexedSeqSet(IndexedSeq.empty ++ t, t)

  override def toSeq: Seq[A] = s
  override def toIndexedSeq[B >: A]: IndexedSeq[B] = s
  override def toSet[B >: A]: Set[B] = t.asInstanceOf[Set[B]]

  def iterator =
    s.iterator
  override def foreach[U](f: A => U): Unit =
    s.foreach(f)
}
