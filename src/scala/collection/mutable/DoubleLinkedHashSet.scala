package scala.collection
package mutable

import generic._

object DoubleLinkedHashSet extends MutableSetFactory[DoubleLinkedHashSet] {
  implicit def canBuildFrom[A]: CanBuildFrom[Coll, A, DoubleLinkedHashSet[A]] = setCanBuildFrom[A]
  override def empty[A]: DoubleLinkedHashSet[A] = new DoubleLinkedHashSet[A]
}

@serializable @SerialVersionUID(1L)
final class DoubleLinkedEntry[A]( val key: A,
    var older: DoubleLinkedEntry[A],
    var younger: DoubleLinkedEntry[A] ) extends HashEntry[A, DoubleLinkedEntry[A]]


@serializable @SerialVersionUID(1L)
class DoubleLinkedHashSet[A] extends Set[A]
                             with GenericSetTemplate[A, DoubleLinkedHashSet]
                             with SetLike[A, DoubleLinkedHashSet[A]]
                             with HashTable[A]{
  override def companion: GenericCompanion[DoubleLinkedHashSet] = DoubleLinkedHashSet

  type Entry = DoubleLinkedEntry[A]
  var oldest, youngest: Entry = null

  override def stringPrefix = "RetSet"
  override def size = tableSize

  override def add(elem:A): Boolean = {
    val oE = findEntry(elem)
    if (oE eq null) {
      val nE = new Entry( elem, youngest, null )

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

  override def foreach[U](f: A =>  U) {
    var cur = oldest
    while ( cur ne null ){
      f(cur.key)
      cur = cur.younger
    }
  }

  override def clone() = new DoubleLinkedHashSet[A] ++= this

  private def writeObject(out: java.io.ObjectOutputStream) {
    serializeTo( out, { v => (v.younger, v.older) } )
  }

  private def readObject(in: java.io.ObjectInputStream) {
    init[(Entry,Entry)](in, { (k,v) => new Entry(k, v._1, v._2) } )
  }
}

object Test{
  def main( args: Array[String] ) {

    val a = DoubleLinkedHashSet.empty ++ List(1,50,2,60,3,70)
    println( "a: " + a.getClass + ": " + a )

    val b = a.take(5).filter(_<10)
    println( "b: " + b.getClass + ": " + b ) // ok

    val bAOS = new java.io.ByteArrayOutputStream()
    val oOS = new java.io.ObjectOutputStream( bAOS )
    oOS.writeObject( a )
    oOS.close
    val bAIS = new java.io.ByteArrayInputStream( bAOS.toByteArray )
    val oIS = new java.io.ObjectInputStream( bAIS )
    val c = oIS.readObject.asInstanceOf[DoubleLinkedHashSet[Int]]
    println( "c: " + c.getClass + "c: " + c ) // ok

    val d = a.view.take(4).force // fail
    println( "d: " + d.getClass + ": " + d )

    val e = a.map(_+1) // fail
    println( "e: " + e.getClass + ": " + e )
  }
}

/*
a: class scala.collection.mutable.DoubleLinkedHashSet: RetSet(1, 50, 2, 60, 3, 70)
b: class scala.collection.mutable.DoubleLinkedHashSet: RetSet(1, 2, 3)
c: class scala.collection.mutable.DoubleLinkedHashSetc: RetSet(1, 50, 2, 60, 3, 70)
d: class scala.collection.mutable.HashSet: Set(1, 50, 60, 2)
e: class scala.collection.mutable.HashSet: Set(71, 3, 61, 51, 4, 2)
*/
