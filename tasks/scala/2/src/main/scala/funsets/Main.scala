package funsets

object Main extends App {
  import FunSets._
  println(contains(singletonSet(1), 1))

  val set1: Set = FunSets.singletonSet(1) // Set {1}
  val set2: Set = FunSets.singletonSet(2) // Set {2}
  val set3: Set = FunSets.union(set1, set2) // Set {1, 2}

  // Testing the union operation
  println("Union of set1 and set2:")
  FunSets.printSet(set3) // Expected: {1, 2}

  // Testing intersection
  val set4: Set = FunSets.intersect(set1, set2) // Set {1} intersects with set {2}, resulting in an empty set
  println("\nIntersection of set1 and set2:")
  FunSets.printSet(set4) // Expected: {}

  // Testing difference
  val set5: Set = FunSets.diff(set3, set1) // Set {1, 2} - {1} = {2}
  println("\nDifference between set3 and set1:")
  FunSets.printSet(set5) // Expected: {2}

  // Testing filter
  val set6: Set = FunSets.filter(set3, x => x % 2 == 0) // Filtering even numbers from {1, 2}
  println("\nFiltered set with even numbers:")
  FunSets.printSet(set6) // Expected: {2}

  // Testing forall
  val set7: Set = FunSets.union(FunSets.singletonSet(1), FunSets.singletonSet(2)) // Set {1, 2}
  val allPositive = FunSets.forall(set7, x => x > 0) // Checking if all elements are greater than 0
  println(s"\nDo all elements in set7 satisfy x > 0? $allPositive") // Expected: true

  // Testing exists
  val existsEven = FunSets.exists(set7, x => x % 2 == 0) // Checking if there is an even number in {1, 2}
  println(s"\nDoes set7 contain an even number? $existsEven") // Expected: true

  // Testing map
  val set8: Set = FunSets.map(set3, x => x * 2) // Applying multiplication by 2 to each element of {1, 2}
  println("\nMapped set with x * 2:")
  FunSets.printSet(set8) // Expected: {2, 4}

}