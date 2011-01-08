package scala.collection
import generic._

trait RetSetLike[A, +This <: RetSet[A] with RetSetLike[A, This]] extends Retaining[A, This] with SetLike[A, This] {
self =>

  override def keySet = repr

  override def firstKey: A = head
  override def lastKey: A = last

}
