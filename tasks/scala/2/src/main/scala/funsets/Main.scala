package funsets

object Main extends App {
  import FunSets._
  println(contains(singletonSet(1), 1)) // true
  println(contains(union(singletonSet(1), singletonSet(2)), 3)) // false
  println(contains(union(singletonSet(1), singletonSet(2)), 2)) // true
  println(contains(intersect(singletonSet(1), singletonSet(2)), 1)) // false
  println(contains(intersect(union(singletonSet(1), singletonSet(2)), singletonSet(2)), 2)) // true
  println(forall((x: Int) => x >= -20 && x % 3 == 0 && x <= 20, (x: Int) => x >= -20)) // true
}
