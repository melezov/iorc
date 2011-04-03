package scala.collection.immutable

import scala.collection.generic.{ImmutableMapFactory, CanBuildFrom, GenericCompanion}

object IndexedSeqMap extends ImmutableMapFactory[IndexedSeqMap] {
  implicit def canBuildFrom[A, B]: CanBuildFrom[Coll, (A, B), IndexedSeqMap[A, B]] = new MapCanBuildFrom[A, B]
  private val Nil = new IndexedSeqMap(IndexedSeq.empty, Map.empty)
  def empty[A, B]: IndexedSeqMap[A, B] = Nil.asInstanceOf[IndexedSeqMap[A, B]]
}

@SerialVersionUID(0x74D4A08461260480L)  // sha1("scala.collection.immutable.IndexedSeqMap-0").take(8)
class IndexedSeqMap[A, +B] private (
    private val _seq: IndexedSeq[(A, B)],
    private val _map: Map[A, B]) extends Map[A, B]
                        with MapLike[A, B, IndexedSeqMap[A, B]]
                        with Serializable{
  override def empty = IndexedSeqMap.empty
  override def stringPrefix = "RetMap"

  override def size: Int = _seq.size

  def get(k: A): Option[B] =
    _map.get(k)

  def +[B1 >: B] (kv: (A, B1)): IndexedSeqMap[A, B1] =
    _map.get(kv._1) match{
/*      If x is reference equal to kv._2, we can return this. */
//      TODO: Solve type wizardry puzzle below.
//      case Some(x: AnyRef) if x eq kv._2 =>
//        this
      case Some(x) =>
        val newSeq = _seq.updated(_seq.indexOf(kv._1), kv)
        new IndexedSeqMap(newSeq, _map + kv)
      case None =>
        new IndexedSeqMap(_seq :+ kv, _map + kv)
    }

  def -(k: A): IndexedSeqMap[A, B] =
    _map.get(k) match{
      case Some(x) =>
        new IndexedSeqMap(_seq.filter(_._1!=k), _map - k)
      case None =>
        this
    }

  def ++[B1 >: B] (that: IndexedSeq[(A, B1)]): IndexedSeqMap[A, B1] =
    if (isEmpty) {
      val newMap = that.toMap
      if (that.size==newMap.size) {
        new IndexedSeqMap(that, newMap)
      }
      else {
        super.++(that)
      }
    }
    else{
      super.++(that)
    }

  def ++[B1 >: B] (that: Map[A, B1]): IndexedSeqMap[A, B1] =
    if (isEmpty) {
      new IndexedSeqMap(that.toIndexedSeq, that)
    }
    else{
      super.++(that)
    }

  override def keySet: IndexedSeqSet[A] = IndexedSeqSet.empty ++ _seq.map(_._1)

  override def toSeq: Seq[(A, B)] = _seq
  override def toIndexedSeq[C >: (A, B)]: IndexedSeq[C] = _seq
  override def toMap[T, U](implicit ev: (A, B) <:< (T, U)): Map[T, U] = _map.asInstanceOf[Map[T, U]]

  def iterator =
    _seq.iterator
  override def foreach[U](f: ((A, B)) => U): Unit =
    _seq.foreach(f)
}
