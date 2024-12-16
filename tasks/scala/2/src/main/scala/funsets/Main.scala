package funsets

object Main extends App {
  import FunSets._

  // Создаем несколько множеств
  val s1 = singletonSet(1)
  val s2 = singletonSet(2)
  val s3 = singletonSet(3)
  
  // Объединяем множества
  val s = union(s1, union(s2, s3))
  
  // Печатаем множество
  printSet(s) // Ожидаемый вывод: {1,2,3}
  
  // Проверяем пересечение
  val sIntersect = intersect(s, singletonSet(2))
  printSet(sIntersect) // Ожидаемый вывод: {2}
  
  // Проверяем разность
  val sDiff = diff(s, singletonSet(1))
  printSet(sDiff) // Ожидаемый вывод: {2,3}
  
  // Фильтрация
  val sFilter = filter(s, x => x > 1)
  printSet(sFilter) // Ожидаемый вывод: {2,3}
  
  // Проверка forall
  println(forall(s, x => x > 0)) // true
  println(forall(s, x => x < 3)) // false
  
  // Проверка exists
  println(exists(s, x => x == 2)) // true
  println(exists(s, x => x == 4)) // false
  
  // Применение map
  val sMap = map(s, x => x * x)
  printSet(sMap) // Ожидаемый вывод: {1,4,9}
}
