package funsets

object Main extends App {
  import FunSets._
  println(contains(singletonSet(1), 1))

  println("Create set oneTwoSet = {1} U {2}")
  val oneTwoSet = union(singletonSet(1), singletonSet(2))
  println("1 in oneTwoSet - " + contains(oneTwoSet, 1))
  println("2 in oneTwoSet - " + contains(oneTwoSet, 2))
  println("3 in oneTwoSet - " + contains(oneTwoSet, 3))

  println("Create set twoThreeSet = {2} U {3}")
  val twoThreeSet = union(singletonSet(2), singletonSet(3))

  println("Create oneTwoSet and twoThreeSet intersection")
  val intersectSet = intersect(oneTwoSet, twoThreeSet)
  println("1 in oneTwoSet intersect twoThreeSet - " + contains(intersectSet, 1))
  println("2 in oneTwoSet intersect twoThreeSet - " + contains(intersectSet, 2))
  println("3 in oneTwoSet intersect twoThreeSet - " + contains(intersectSet, 3))

  println("Create oneTwoSet and twoThreeSet difference")
  val diffSet = diff(oneTwoSet, twoThreeSet)
  println("1 in diffSet - " + contains(diffSet, 1))
  println("2 in diffSet - " + contains(diffSet, 2))
  println("3 in diffSet - " + contains(diffSet, 3))

  println("Create bigSet = oneTwoSet U twoThreeSet")
  val bigSet = union(oneTwoSet, twoThreeSet)
  println("Filter bigSet with predicate { x >= 2 }")
  val filteredSet = filter(bigSet, x => x >= 2)
  println("1 in filteredSet - " + contains(filteredSet, 1))
  println("2 in filteredSet - " + contains(filteredSet, 2))
  println("3 in filteredSet - " + contains(filteredSet, 3))

  println("Test predicate { x < 3 } for all elements in different sets")
  val testPredicate: (Int => Boolean) = x => x < 3
  println("On oneTwoSet - " + forall(oneTwoSet, testPredicate))
  println("On twoThreeSet - " + forall(twoThreeSet, testPredicate))
  println("On bigSet - " + forall(bigSet, testPredicate))

  println("\nTesting exists function:")
  println("Exists element > 2 in oneTwoSet: " + exists(oneTwoSet, x => x > 2))
  println("Exists element > 2 in twoThreeSet: " + exists(twoThreeSet, x => x > 2))
  println("Exists element = 3 in bigSet: " + exists(bigSet, x => x == 3))
  println("Exists element = 4 in bigSet: " + exists(bigSet, x => x == 4))
}
