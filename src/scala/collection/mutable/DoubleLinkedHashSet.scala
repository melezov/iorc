package scala.collection
package mutable

import generic._

object DoubleLinkedHashSet extends MutableSetFactory[DoubleLinkedHashSet] {
  implicit def canBuildFrom[A]: CanBuildFrom[Coll, A, DoubleLinkedHashSet[A]] = setCanBuildFrom[A]
  override def empty[A]: DoubleLinkedHashSet[A] = new DoubleLinkedHashSet[A]
}

final class DoubleLinkedEntry[A]( val key: A,
    var younger: DoubleLinkedEntry[A],
    var older: DoubleLinkedEntry[A] ) extends HashEntry[A, DoubleLinkedEntry[A]]

class DoubleLinkedHashSet[A] extends Set[A]
                             with GenericSetTemplate[A, DoubleLinkedHashSet]
                             with SetLike[A, DoubleLinkedHashSet[A]]
                             with HashTable[A]{
  override def companion: GenericCompanion[DoubleLinkedHashSet] = DoubleLinkedHashSet

  type Entry = DoubleLinkedEntry[A]
  var oldest, youngest: Entry = null

  override def stringPrefix = "RetSet"
  override def size = tableSize

  override def add(elem: A): Boolean = {
    val oE = findEntry(elem)
    if (oE eq null) {
      val nE = new Entry( elem, null, youngest )

      if ( oldest eq null ){
        oldest = nE
      }
      else{
        youngest.younger = nE
      }

      youngest = nE
      addEntry( nE )
      true
    }
    else false
  }

  override def remove(elem: A): Boolean = {
    val oE = removeEntry(elem)
    if ( oE ne null ){
      if ( oldest eq youngest ){
        youngest = null
        oldest = null
      }
      else if ( oE eq oldest ){
        oE.younger.older = null
        oldest = oE.younger
      }
      else if ( oE eq youngest ){
        oE.older.younger = null
        youngest = oE.older
      }
      else{
        oE.younger.older = oE.older
        oE.older.younger = oE.younger
      }
      true
    }
    else false
  }

  def += (elem: A): this.type = {
    add(elem)
    this
  }

  def -= (elem: A): this.type = {
    remove(elem)
    this
  }

  def contains(elem: A): Boolean = {
    findEntry(elem) ne null
  }

  override def clear() {
    oldest = null
    youngest = null
    clearTable()
  }

  def iterator = new Iterator[A]{
    var cur = oldest
    def next = {
      val key = cur.key
      cur = cur.younger
      key
    }
    def hasNext = cur ne null
  }
}

object Test{
  def main( args: Array[String] ) {

    val a = DoubleLinkedHashSet.empty ++ List(1,50,2,60,3,70)
    println( a.getClass + ": " + a )

    val b = a.take(5).filter(_<10)
    println( b.getClass + ": " + b ) // ok

    val c = a.view.take(4).force // fail
    println( c.getClass + ": " + c )

    val d = a.map(_+1) // fail
    println( d.getClass + ": " + d )

    val e = a.groupBy(identity) // fail
    println( e.getClass + ": " + e )

    val f = a.map(_*23) // fail
    println( f.getClass + ": " + f )
  }
}

/*
  class scala.collection.mutable.DoubleLinkedHashSet: RetSet(1, 50, 2, 60, 3, 70)
  class scala.collection.mutable.DoubleLinkedHashSet: RetSet(1, 2, 3)
  class scala.collection.mutable.HashSet: Set(1, 50, 60, 2)
  class scala.collection.mutable.HashSet: Set(71, 3, 61, 51, 4, 2)
*/
