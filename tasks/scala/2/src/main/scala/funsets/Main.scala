package funsets

object Main extends App {
  import FunSets._
  println(contains(singletonSet(1), 1))

  println("Create set oneTwoSet = {1} U {2}")
  val oneTwoSet = union(singletonSet(1), singletonSet(2))
  println("1 in oneTwoSet - " + contains(oneTwoSet, 1))
  println("2 in oneTwoSet - " + contains(oneTwoSet, 2))
  println("3 in oneTwoSet - " + contains(oneTwoSet, 3))
}
