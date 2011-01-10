import scala.collection.immutable.IndexedSeqHashSet
import scala.collection.mutable.DoubleLinkedHashSet

object Main {
  def main(args: Array[String]): Unit = {
    mutableRetSetTest
    immutableRetSetTest
  }

  def immutableRetSetTest = {
    var a = IndexedSeqHashSet.empty ++ List(50,4,60,3)

    a += 99
    a += 98
    a += 99

    a += 1
    a += 2
    a += 1

    println( "a: " + a.getClass + ": " + a )

    val b = a.take(3).filter(_<10)
    println( "b: " + b.getClass + ": " + b )

    val bAOS = new java.io.ByteArrayOutputStream()
    val oOS = new java.io.ObjectOutputStream( bAOS )
    oOS.writeObject( a )
    oOS.close

    val serForm = bAOS.toByteArray
    println( serForm.length )

    new java.io.FileOutputStream( "R:\\a.bin" ){
      write( serForm )
      close()
    }

    val bAIS = new java.io.ByteArrayInputStream( serForm )
    val oIS = new java.io.ObjectInputStream( bAIS )
    val c = oIS.readObject.asInstanceOf[IndexedSeqHashSet[Int]]

    val d = c -- List(1,2,99)
    println( "c: " + c.getClass + ": " + c )
    println( "d: " + c.getClass + ": " + d )

    val e = a.view.take(4).force
    println( "e: " + e.getClass + ": " + e )

    val f = a.map( _+1 )
    println( "f: " + f.getClass + ": " + f )

/*
  a: class scala.collection.immutable.IndexedSeqHashSet: RetSet(50, 4, 60, 3, 99, 98, 1, 2)
  b: class scala.collection.immutable.IndexedSeqHashSet: RetSet(4)
  c: class scala.collection.immutable.IndexedSeqHashSet: RetSet(50, 4, 60, 3, 99, 98, 1, 2)
  d: class scala.collection.immutable.IndexedSeqHashSet: RetSet(50, 4, 60, 3, 98)
  e: class scala.collection.immutable.IndexedSeqHashSet: RetSet(50, 4, 60, 3)
  f: class scala.collection.immutable.IndexedSeqHashSet: RetSet(51, 5, 61, 4, 100, 99, 2, 3)
*/
  }

  def mutableRetSetTest = {
    val a = DoubleLinkedHashSet.empty ++ List(50,4,60,3)

    a.addYoungest( 99 )
    a.addYoungest( 98 )
    a.forceYoungest( 99 )

    a.addOldest( 1 )
    a.addOldest( 2 )
    a.forceOldest( 1 )

    println( "a: " + a.getClass + ": " + a )

    val b = a.take(3).filter(_<10)
    println( "b: " + b.getClass + ": " + b )

    val bAOS = new java.io.ByteArrayOutputStream()
    val oOS = new java.io.ObjectOutputStream( bAOS )
    oOS.writeObject( a )
    oOS.close

    val serForm = bAOS.toByteArray
    println( serForm.length )

    val bAIS = new java.io.ByteArrayInputStream( serForm )
    val oIS = new java.io.ObjectInputStream( bAIS )
    val c = oIS.readObject.asInstanceOf[DoubleLinkedHashSet[Int]]

    val d = c.clone --= List(1,2,99)
    println( "c: " + c.getClass + ": " + c )
    println( "d: " + c.getClass + ": " + d )

    val e = a.view.take(4).force
    println( "e: " + e.getClass + ": " + e )

    val f = a.map( _+1 )
    println( "f: " + f.getClass + ": " + f )

/*
  a: class scala.collection.mutable.DoubleLinkedHashSet: RetSet(1, 2, 50, 4, 60, 3, 98, 99)
  b: class scala.collection.mutable.DoubleLinkedHashSet: RetSet(1, 2)
  c: class scala.collection.mutable.DoubleLinkedHashSet: RetSet(1, 2, 50, 4, 60, 3, 98, 99)
  d: class scala.collection.mutable.DoubleLinkedHashSet: RetSet(50, 4, 60, 3, 98)
  e: class scala.collection.mutable.DoubleLinkedHashSet: RetSet(1, 2, 50, 4)
  f: class scala.collection.mutable.DoubleLinkedHashSet: RetSet(2, 3, 51, 5, 61, 4, 99, 100)
*/
  }
}

