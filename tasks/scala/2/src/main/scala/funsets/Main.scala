  package funsets

object Main extends App {
  import FunSets._
  println(contains(singletonSet(1), 1))

  val unionSet = union(singletonSet(1), singletonSet(2))
  val intersectSet = intersect(unionSet, singletonSet(1))
  val diffSet = diff(unionSet, singletonSet(1))
  val filteredSet = filter(unionSet, x => x % 2 == 0)

  println("SingletonSet 1: " + FunSets.toString(singletonSet(1)))
  println("Union of {1} and {2}: " + FunSets.toString(unionSet))
  println("Intersection of union with {1}: " + FunSets.toString(intersectSet))
  println("Difference of union and {1}: " + FunSets.toString(diffSet))
  println("Filter union for even numbers: " + FunSets.toString(filteredSet))

  println("Forall in union (x < 3): " + forall(unionSet, x => x < 3))
  println("Exists in union (x == 2): " + exists(unionSet, x => x == 2))

  val mappedSet = map(unionSet, x => x * 2)
  println("Mapped union (x * 2): " + FunSets.toString(mappedSet))

}
