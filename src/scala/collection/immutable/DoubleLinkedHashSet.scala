package scala.collection
package mutable

import generic._

object DoubleLinkedHashSet

final class DoubleLinkedEntry[A](
    val key: A,
    var next: DoubleLinkedEntry[A],
    var prev: DoubleLinkedEntry[A] ) extends HashEntry[A, DoubleLinkedEntry[A]]{
  def this( key: A ) = this( key, null, null )
}

class DoubleLinkedHashSet[A] extends Set[A] with HashTable[A] {
  type Entry = DoubleLinkedEntry[A]

  var header, footer: DoubleLinkedEntry[A] = null

  def += (elem: A): this.type = {
    val oE = findEntry(elem)
    if (oE eq null) {
      val nE = new Entry( elem )
      if ( footer eq null ){
        
      }
      
      
      val prev = if ( footer eq null ) null else {footer.prev

      val nE = new Entry( elem, footer.prev, footer )
      addEntry( nE )
      footer = nE
      if (header eq null ){
        header = nE
      }
    }
    this
  }

  def -= (elem: A): this.type = {
    val oE = removeEntry(elem)
    if (oE ne null){
      oE.prev =
    }
    this
  }

  def contains(elem: A): Boolean = false

  def iterator = null
}

object Test{
  def main( args: Array[String] ) {

    val a = new DoubleLinkedHashSet[Int]()
    println( a.getClass + ": " + a )

    a -= 3

    val b = a
    println( b.getClass + ": " + b )

    val c = a.take(2)
    println( c.getClass + ": " + c )

  }
}