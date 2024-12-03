package funsets

object Main extends App {
  import FunSets._
  val s1 = singletonSet(1)
  val s2 = singletonSet(2)
  val s3 = union(s1, s2)
  val s4 = filter(s3, x => x % 2 == 0)

  println(contains(s1, 1)) // true
  println(contains(s2, 1)) // false
  println(toString(s3))    // {1,2}
  println(toString(s4))    // {2}
}
