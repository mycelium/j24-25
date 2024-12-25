package funsets

object Main extends App {
  import FunSets._
  println(contains(singletonSet(1), 1))

  println("union")
  println(FunSets.toString(union(singletonSet(4), singletonSet(6))))

  println("intersect")
  println(FunSets.toString(intersect(singletonSet(1), singletonSet(2))))
  println(FunSets.toString(intersect(union(singletonSet(4), singletonSet(2)), singletonSet(4))))

  println("diff")
  println(FunSets.toString(diff(union(singletonSet(4), singletonSet(2)),
    union(singletonSet(4), singletonSet(3)))))

  println("filter")
  println(FunSets.toString(filter(union(singletonSet(-3), singletonSet(5)), (x: Int) => x > 0)))

  println("forall")
  println(FunSets.toString((x: Int) => x >= -100 && x % 10 == 0 && x <= 100))
  println(forall((x: Int) => x >= -100 && x % 10 == 0 && x <= 100, (x: Int) => x % 2 == 0))

  println("exists")
  println(FunSets.toString((x: Int) => x >= -100 && x % 10 == 0 && x <= 100))
  println(exists((x: Int) => x >= -100 && x % 10 == 0 && x <= 100, (x: Int) => x == 90))

  println("map")
  println(FunSets.toString((x: Int) => x >= 0 && x <= 10))
  println(FunSets.toString(map((x: Int) => x >= 0 && x <= 10, (x: Int) => x + 2)))

}
