package funsets

import common._

/**
 * 2. Purely Functional Sets.
 */
object FunSets {
  /**
   * We represent a set by its characteristic function, i.e.
   * its `contains` predicate.
   */
  type Set = Int => Boolean

  /**
   * Indicates whether a set contains a given element.
   */
  def contains(s: Set, elem: Int): Boolean = s(elem)

  /**
   * Returns the set of the one given element.
   */
    def singletonSet(elem: Int): Set = external  => external == elem // множество из elem, с которым сравниваем "внешние" значения
  

  /**
   * Returns the union of the two given sets,
   * the sets of all elements that are in either `s` or `t`.
   */
    def union(s: Set, t: Set): Set = external => contains(s, external) || contains(t, external) // проверяем наличие в любом из множеств
  
  /**
   * Returns the intersection of the two given sets,
   * the set of all elements that are both in `s` and `t`.
   */
    def intersect(s: Set, t: Set): Set = external => contains(s, external) && contains(t, external) // проверяем наличие в обеих множествах
  
  /**
   * Returns the difference of the two given sets,
   * the set of all elements of `s` that are not in `t`.
   */
    def diff(s: Set, t: Set): Set = external => contains(s, external) && !contains(t, external) // наличие в `s` и отсутствие в `t`
  
  /**
   * Returns the subset of `s` for which `p` holds.
   */
    def filter(s: Set, p: Int => Boolean): Set = intersect(s, p) //
  

  /**
   * The bounds for `forall` and `exists` are +/- 1000.
   */
  val bound = 1000

  /**
   * Returns whether all bounded integers within `s` satisfy `p`.
   */
    def forall(s: Set, p: Int => Boolean): Boolean = {
    def iter(a: Int): Boolean = {
      if (a > bound) true // раз дошли до границы, то все числа до этого прошли проверку
      else if (diff(s, p)(a)) false // число есть в множестве и не прошло проверку
      else iter(a + 1)
    }
    iter(-bound) // проверяем все числа [-bound; bound]
  }
  
  /**
   * Returns whether there exists a bounded integer within `s`
   * that satisfies `p`.
   */
    def exists(s: Set, p: Int => Boolean): Boolean = {
      def iter(a: Int): Boolean = {
        if (a > bound) false // раз дошли до границы, то все числа не подошли
        else if (intersect(s, p)(a)) true // число есть в мн-ве `s` и прошло проверку
        else iter(a + 1)
      }
      iter(-bound)
    }
  
  /**
   * Returns a set transformed by applying `f` to each element of `s`.
   */
    def map(s: Set, f: Int => Int): Set = external => exists(s, a => f(a) == external) // проверяем существование такого элемента external,
                                                                                       // что он есть в мн-ве f(s)
  
  /**
   * Displays the contents of a set
   */
  def toString(s: Set): String = {
    val xs = for (i <- -bound to bound if contains(s, i)) yield i
    xs.mkString("{", ",", "}")
  }

  /**
   * Prints the contents of a set on the console.
   */
  def printSet(s: Set) {
    println(toString(s))
  }
}
