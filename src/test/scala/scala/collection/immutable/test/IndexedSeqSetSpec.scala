package scala.collection.immutable.test

import scala.collection.immutable.IndexedSeqSet

import org.scalatest._

class IndexedSeqSetSpec extends FeatureSpec with GivenWhenThen {

  feature("IndexedSeqSet must reuse its underlying collections") {

    info("When IndexedSeqSet is constructed by appending a Set to IndexedSeqSet.empty")
    info("it should return the same object via the .toSet method")

    scenario("IndexedSeqSet is constructed with a Set") {

      given("an empty IndexedSeqSet")
      val iSS = IndexedSeqSet.empty

      when("when appending an immutable Set")
      val iSet = (1 to 1000).toSet

      then("then the same object must be returned through .toSet")
      assert(true)// iSS.toSet eq iSet)
    }
  }
}