package funsets

object Main extends App {
  import FunSets._
  println(contains(singletonSet(1), 1))
  println(FunSets.toString(map(union(singletonSet(1), singletonSet(2)), x => x + 1)))
}
