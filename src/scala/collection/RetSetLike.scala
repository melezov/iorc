/*
package scala.collection
import generic._

trait RetSetLike[A, +This <: RetSet[A] with RetSetLike[A, This]] extends Retaining[A] with SetLike[A, This] {
self =>

  override def keySet = repr

  override def youngestKey: A = head
  override def oldestKey: A = last

}
*/