package scala.collection
package generic

trait Retaining[K, +This] {

  /** The current collection */
  protected def repr: This

  /** return as a projection the set of keys in this collection */
  def keySet: RetSet[K]

  /** Returns the first key of the collection. */
  def firstKey: K

  /** Returns the last key of the collection. */
  def lastKey: K
}
