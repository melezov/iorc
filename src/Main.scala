import scala.collection.mutable.DoubleLinkedHashSet

object Main {
  def main(args: Array[String]): Unit = {

    val a = DoubleLinkedHashSet.empty ++ List(50,4,60,3)

    a.addYoungest( 99 )
    a.addYoungest( 98 )
    a.forceYoungest( 99 )

    a.addOldest( 1 )
    a.addOldest( 2 )
    a.forceOldest( 1 )

    println( "a: " + a.getClass + ": " + a )

    val b = a.take(3).filter(_<10)
    println( "b: " + b.getClass + ": " + b ) // type ok

    val bAOS = new java.io.ByteArrayOutputStream()
    val oOS = new java.io.ObjectOutputStream( bAOS )
    oOS.writeObject( a )
    oOS.close

    new java.io.FileOutputStream( "R:\\a.bin" ){
      this.write( bAOS.toByteArray )
      close()
    }

    val bAIS = new java.io.ByteArrayInputStream( bAOS.toByteArray )
    val oIS = new java.io.ObjectInputStream( bAIS )
    val c = oIS.readObject.asInstanceOf[DoubleLinkedHashSet[Int]]

    val d = c.clone --= List(1,2,99)
    println( "c: " + c.getClass + ": " + c ) // serialization ok
    println( "d: " + c.getClass + ": " + d ) // clone ok (must differ from c)

    val e = a.view.take(4).force
    println( "e: " + e.getClass + ": " + e )

    val f = a.map( _+1 )
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
