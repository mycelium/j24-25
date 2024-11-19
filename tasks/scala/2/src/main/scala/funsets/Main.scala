package funsets

object Main extends App {
  import FunSets._
  println(contains(singletonSet(1), 1))

  /*
  val s1 = singletonSet(1)
  val s2 = singletonSet(2)
  val s3 = singletonSet(3)

  val u12 = union(s1, s2)
  val u123 = union(u12, s3)

  val i23 = intersect(s2, s3)
  val d13 = diff(s1, s3)

  println("u12: " + FunSets.toString(u12))
  println("u123: " + FunSets.toString(u123))
  println("i23: " + FunSets.toString(i23))
  println("d13: " + FunSets.toString(d13))

  val isEven: Int => Boolean = x => x % 2 == 0
  println("u123 evens: " + FunSets.toString(filter(u123, isEven)))

  println("forall u123 greater than 2: " + forall(u123, x => x > 2))
  println("exists u123 even: " + exists(u123, isEven))

  val doubled = map(u123, x => x * 2)
  println("doubled: " + FunSets.toString(doubled))
   */
}
