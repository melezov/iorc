package scala.collection
import generic._

trait RetSet[A] extends Set[A] with RetSetLike[A, SortedSet[A]] {
  override def empty: RetSet[A] = RetSet.empty[A]
}

object RetSet extends RetSetFactory[RetSet] {
  def empty[A]: immutable.RetSet[A] = immutable.RetSet.empty[A]
  implicit def canBuildFrom[A]: CanBuildFrom[Coll, A, RetSet[A]] = new RetSetCanBuildFrom[A]
}

