package funsets

import common._

/**
 * 2. Purely Functional Sets.
 */
object FunSets {
  /**
   * Мы представляем множество как его характеристическую функцию, т.е.
   * предикат `contains`.
   */
  type Set = Int => Boolean

  /**
   * Проверяет, содержит ли множество `s` элемент `elem`.
   */
  def contains(s: Set, elem: Int): Boolean = s(elem)

  /**
   * Возвращает множество, содержащее только один заданный элемент.
   */
  def singletonSet(elem: Int): Set = (x: Int) => x == elem

  /**
   * Возвращает объединение двух множеств `s` и `t`,
   * т.е. множество всех элементов, которые находятся либо в `s`, либо в `t`.
   */
  def union(s: Set, t: Set): Set = (x: Int) => s(x) || t(x)

  /**
   * Возвращает пересечение двух множеств `s` и `t`,
   * т.е. множество всех элементов, которые находятся одновременно в `s` и в `t`.
   */
  def intersect(s: Set, t: Set): Set = (x: Int) => s(x) && t(x)

  /**
   * Возвращает разность двух множеств `s` и `t`,
   * т.е. множество всех элементов `s`, которых нет в `t`.
   */
  def diff(s: Set, t: Set): Set = (x: Int) => s(x) && !t(x)

  /**
   * Возвращает подмножество `s`, для которого предикат `p` выполняется.
   */
  def filter(s: Set, p: Int => Boolean): Set = (x: Int) => s(x) && p(x)

  /**
   * Границы для функций `forall` и `exists` - +/- 1000.
   */
  val bound = 1000

  /**
   * Проверяет, выполняется ли предикат `p` для всех элементов множества `s` в пределах границ.
   */
  def forall(s: Set, p: Int => Boolean): Boolean = {
    def iter(a: Int): Boolean = {
      if (a > bound) true
      else if (s(a) && !p(a)) false
      else iter(a + 1)
    }
    iter(-bound)
  }

  /**
   * Проверяет, существует ли элемент в множестве `s`, для которого предикат `p` выполняется.
   */
  def exists(s: Set, p: Int => Boolean): Boolean = {
    !forall(s, (x: Int) => !p(x))
  }

  /**
   * Возвращает множество, полученное применением функции `f` к каждому элементу множества `s`.
   */
  def map(s: Set, f: Int => Int): Set = (y: Int) => exists(s, (x: Int) => f(x) == y)

  /**
   * Отображает содержимое множества в виде строки.
   */
  def toString(s: Set): String = {
    val xs = for (i <- -bound to bound if contains(s, i)) yield i
    xs.mkString("{", ",", "}")
  }

  /**
   * Выводит содержимое множества на консоль.
   */
  def printSet(s: Set) {
    println(toString(s))
  }
}
