package funsets

object Main extends App {
  import FunSets._
  println(contains(singletonSet(1), 1))

  /*
  // Создание множеств
    val set1 = singletonSet(1)
    val set2 = singletonSet(2)
    val set3 = union(set1, set2) // {1, 2}
    val set4 = union(set3, singletonSet(3)) // {1, 2, 3}

    // Печать множеств
    println("Set 1: ")
    printSet(set1) // {1}
    println("Set 2: ")
    printSet(set2) // {2}
    println("Set 3 (Union of Set 1 and Set 2): ")
    printSet(set3) // {1, 2}
    println("Set 4 (Union of Set 3 and singleton(3)): ")
    printSet(set4) // {1, 2, 3}

    // Пересечение множеств
    val intersectedSet = intersect(set3, singletonSet(2)) // {2}
    println("Intersected Set (Set 3 and singleton(2)): ")
    printSet(intersectedSet)

    // Разность множеств
    val diffSet = diff(set4, singletonSet(2)) // {1, 3}
    println("Difference Set (Set 4 - singleton(2)): ")
    printSet(diffSet)

    // Фильтрация
    val filteredSet = filter(set4, x => x % 2 == 0) // {2}
    println("Filtered Set (even numbers from Set 4): ")
    printSet(filteredSet)

    // forall
    println("Does Set 4 contain only even numbers? " + forall(set4, x => x % 2 == 0)) // false

    // exists
    println("Does Set 4 contain any even numbers? " + exists(set4, x => x % 2 == 0)) // true

    // map
    val mappedSet = map(set4, x => x * 2) // {2, 4, 6}
    println("Mapped Set (each element multiplied by 2): ")
    printSet(mappedSet)
  */
}
