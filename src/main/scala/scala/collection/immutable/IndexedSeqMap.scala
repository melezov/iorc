package scala.collection.immutable

import scala.collection.generic.{ImmutableMapFactory, CanBuildFrom, GenericCompanion}

object IndexedSeqMap extends ImmutableMapFactory[IndexedSeqMap] {
  implicit def canBuildFrom[A, B]: CanBuildFrom[Coll, (A, B), IndexedSeqMap[A, B]] = new MapCanBuildFrom[A, B]
  private val Nil = new IndexedSeqMap(IndexedSeq.empty, Map.empty)
  def empty[A, B]: IndexedSeqMap[A, B] = Nil.asInstanceOf[IndexedSeqMap[A, B]]
}

@serializable @SerialVersionUID(0x74D4A08461260480L) // scala.collection.immutable.IndexedSeqMap-0
class IndexedSeqMap[A, +B] private (
    val s: IndexedSeq[(A, B)],
    val m: Map[A, B]) extends Map[A, B]
                        with MapLike[A, B, IndexedSeqMap[A, B]]{
//                        with Serializable{
  override def empty = IndexedSeqMap.empty
  override def stringPrefix = "RetMap"

    override def size: Int = s.size

  def get(k: A): Option[B] =
    m.get(k)

  def +[B1 >: B] (kv: (A, B1)): IndexedSeqMap[A, B1] = {
    val newSeq = if (m.contains(kv._1)) {
// TODO: If kv._2 is referrentialy equal to the result of m(kv._1),
//       we could return `this` (no update necessary)
      s.updated(s.indexOf(kv._1),kv)
    } else {
      s :+ kv
    }
    val newMap = m + kv
    new IndexedSeqMap(newSeq, newMap)
  }

  def -(k: A): IndexedSeqMap[A, B] =
    m.get(k) match{
      case Some(x) =>
        new IndexedSeqMap(s.filter(_._1!=k), m - k)
      case None =>
        this
    }

  def iterator =
    s.iterator
  override def foreach[U](f: ((A, B)) => U): Unit =
    s.foreach(f)

  override def toSeq: Seq[(A, B)] = s
  override def toIndexedSeq[C >: (A, B)]: IndexedSeq[C] = s
  override def toMap[T, U](implicit ev: (A, B) <:< (T, U)): Map[T, U] = m.asInstanceOf[Map[T, U]]
}
