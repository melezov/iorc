package scala.collection
package mutable

import generic._

object DoubleLinkedHashSet extends MutableSetFactory[DoubleLinkedHashSet] {
  implicit def canBuildFrom[A]: CanBuildFrom[Coll, A, DoubleLinkedHashSet[A]] = setCanBuildFrom[A]
  override def empty[A]: DoubleLinkedHashSet[A] = new DoubleLinkedHashSet[A]
}

final class DoubleLinkedEntry[A](
    val key: A,
    var older: DoubleLinkedEntry[A] = null,
    var younger: DoubleLinkedEntry[A] = null
    ) extends HashEntry[A, DoubleLinkedEntry[A]]


@serializable @SerialVersionUID(0xECF65284E8C022B8L)
class DoubleLinkedHashSet[A] extends Set[A]
                             with GenericSetTemplate[A, DoubleLinkedHashSet]
                             with SetLike[A, DoubleLinkedHashSet[A]]
                             with HashTable[A]{
  override def companion: GenericCompanion[DoubleLinkedHashSet] = DoubleLinkedHashSet

  type Entry = DoubleLinkedEntry[A]

  @transient
  var oldest, youngest: Entry = null

  // part of the RetSet family (Insertion Order Retaining collections)
  override def stringPrefix = "RetSet"
  override def size = tableSize

  override def add(elem:A): Boolean = {
    val oE = findEntry(elem)
    if (oE eq null) {
      val nE = new Entry( elem, youngest )

      // if oldest is null then this is our first element
      if ( oldest eq null ){
        oldest = nE
      }
      // otherwise, reroute the previous youngest
      else{
        youngest.younger = nE
      }

      // new elements will always be youngest
      youngest = nE
      addEntry( nE )
      true
    }
    else false
  }

  override def remove(elem: A): Boolean = {
    val oE = removeEntry(elem)
    if ( oE ne null ){
      // if it was the last element, oldest will point to youngest
      if ( oldest eq youngest ){
        youngest = null
        oldest = null
      }
      // check and modify oldest boundary
      else if ( oE eq oldest ){
        oE.younger.older = null
        oldest = oE.younger
      }
      // check and modify youngest boundary
      else if ( oE eq youngest ){
        oE.older.younger = null
        youngest = oE.older
      }
      // the removal was from the middle of the list
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
    clearTable()
    // garbage collect the list
    oldest = null
    youngest = null
  }

  /** Changing the collection whilst using the iterator will cause
   *  non-deterministic behavior due to mutability of all underlying
   *  structures.
   */
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

  override def clone() = new DoubleLinkedHashSet[A]() ++= this

  /** Serialization is performed by writing down the older
   *  and younger keys surrounding each element. This could be
   *  optimized by for instance, writing only one of them and
   *  rebuilding the structure in reverse for the other element,
   *  thus saving more than half of the added serialization
   *  overhead. Will be changed in the future.
   */
  private def writeObject(out: java.io.ObjectOutputStream) {
    serializeTo( out, v => (
      if ( v.older eq null ) null else v.older.key,
      if ( v.younger eq null ) null else v.younger.key
    ) )
  }

  private def readObject(in: java.io.ObjectInputStream) {

    // temporary Map for initializing Entry links
    val tmp = new LinkedHashMap[Entry,(A,A)]()

    // first, instantiate all Entry elements with empty links
    init[(A,A)](in, { (k,v) =>
      val nE = new Entry( k )
      tmp(nE) = v
      nE
    })

    // now link up the Entry elements
    tmp.foreach{ kv =>
      val entry = kv._1
      val older = kv._2._1
      val younger = kv._2._2

      if ( older != null ){
        entry.older = findEntry(older)
      }
      else {
        // if no older, set the root element link
        oldest = entry
      }

      if ( younger != null ){
        entry.younger = findEntry(younger)
      }
      else {
        // if no younger, set the root element link
        youngest = entry
      }
    }
  }
}

object Test{
  def main( args: Array[String] ) {

    val a = DoubleLinkedHashSet.empty ++ List(1,50,2,60,3,70)
    println( "a: " + a.getClass + ": " + a )

    val b = a.take(5).filter(_<10)
    println( "b: " + b.getClass + ": " + b ) // type ok

    val bAOS = new java.io.ByteArrayOutputStream()
    val oOS = new java.io.ObjectOutputStream( bAOS )
    oOS.writeObject( a )
    oOS.close

    val bAIS = new java.io.ByteArrayInputStream( bAOS.toByteArray )
    val oIS = new java.io.ObjectInputStream( bAIS )
    val c = oIS.readObject.asInstanceOf[DoubleLinkedHashSet[Int]]
    val d = c.clone --= List(1,2,70)
    println( "c: " + c.getClass + ": " + c ) // serialization ok
    println( "d: " + c.getClass + ": " + d ) // clone ok (must differ from c)

    val e = a.clone.view.take(4).force // fail - returns HashSet instead of DoubleLinkedHashSet
    println( "e: " + e.getClass + ": " + e )

    val f = a.map( _+1 ) // fail - returns HashSet instead of DoubleLinkedHashSet
    println( "f: " + f.getClass + ": " + f )
  }
}

/*
  a: class scala.collection.mutable.DoubleLinkedHashSet: RetSet(1, 50, 2, 60, 3, 70)
  b: class scala.collection.mutable.DoubleLinkedHashSet: RetSet(1, 2, 3)
  c: class scala.collection.mutable.DoubleLinkedHashSet: RetSet(1, 50, 2, 60, 3, 70)
  d: class scala.collection.mutable.DoubleLinkedHashSet: RetSet(50, 60, 3)
  e: class scala.collection.mutable.HashSet: Set(1, 50, 60, 2)
  f: class scala.collection.mutable.HashSet: Set(71, 3, 61, 51, 4, 2)
*/
