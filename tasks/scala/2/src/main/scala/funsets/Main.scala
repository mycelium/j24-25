package funsets

object Main extends App {
  import FunSets._
  println("contains")
  println(contains(singletonSet(1), 1)) // true

  println("\nunion")
  println(FunSets.toString(union(singletonSet(1), singletonSet(2)))) // {1, 2}

  println("\nintersect")
  println(FunSets.toString(intersect(singletonSet(1), singletonSet(2)))) // {}
  println(FunSets.toString(intersect(union(singletonSet(1), singletonSet(2)), singletonSet(2)))) // {2}

  println("\nforall")
  println(FunSets.toString((x: Int) => x >= -20 && x % 3 == 0 && x <= 20))
  println(forall((x: Int) => x >= -20 && x % 3 == 0 && x <= 20, (x: Int) => x >= -20)) // true

  println("\nexists")
  println(FunSets.toString((x: Int) => x >= -20 && x % 3 == 0 && x <= 20))
  println(exists((x: Int) => x >= -20 && x % 3 == 0 && x <= 20, (x: Int) => x % 2 == 0)) // true

  println("\nmap")
  println(FunSets.toString((x: Int) => x >= 1 && x <= 3))
  println(FunSets.toString(map((x: Int) => x >= 1 && x <= 3, (x: Int) => x * x))) // true
}
