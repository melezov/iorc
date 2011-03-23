package scala.collection.immutable

import scala.collection.generic.{ImmutableMapFactory, CanBuildFrom, GenericCompanion}

object IndexedSeqMap extends ImmutableMapFactory[IndexedSeqMap] {
  implicit def canBuildFrom[A, B]: CanBuildFrom[Coll, (A, B), IndexedSeqMap[A, B]] = new MapCanBuildFrom[A, B]
  private val Nil = new IndexedSeqMap(IndexedSeq.empty, Map.empty)
  def empty[A, B]: IndexedSeqMap[A, B] = Nil.asInstanceOf[IndexedSeqMap[A, B]]
}

@serializable @SerialVersionUID(0x74D4A08461260480L) // scala.collection.immutable.IndexedSeqMap-0
class IndexedSeqMap[A, +B] private (
    val seq: IndexedSeq[(A, B)],
    val map: Map[A, B]) extends Map[A, B]
                        with MapLike[A, B, IndexedSeqMap[A, B]]{
  override def empty = IndexedSeqMap.empty
  override def stringPrefix = "RetMap"

    override def size: Int = seq.size

  def get(k: A): Option[B] =
    map.get(k)

  def +[B1 >: B] (kv: (A, B1)): IndexedSeqMap[A, B1] = {
    val newSeq = if (map.contains(kv._1)) {
// TODO: If kv._2 is referrentialy equal to the result of map(kv._1),
//       we could return `this` (no update necessary)
      seq.updated(seq.indexOf(kv._1),kv)
    } else {
      seq :+ kv
    }
    val newMap = map + kv
    new IndexedSeqMap(newSeq, newMap)
  }

  def -(k: A): IndexedSeqMap[A, B] =
    map.get(k) match{
      case Some(x) =>
        new IndexedSeqMap(seq.filter(_._1!=k), map - k)
      case None =>
        this
    }

  def iterator =
    seq.iterator
  override def foreach[U](f: ((A, B)) => U): Unit =
    seq.foreach(f)

  override def toSeq: Seq[(A, B)] = seq
  override def toIndexedSeq[C >: (A, B)]: IndexedSeq[C] = seq
  override def toMap[T, U](implicit ev: (A, B) <:< (T, U)): Map[T, U] = map.asInstanceOf[Map[T, U]]
}
