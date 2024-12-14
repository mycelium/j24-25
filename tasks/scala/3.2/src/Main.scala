object Main {
  def main(args: Array[String]): Unit = {

    val nestedList1 = List(List(1, 2), 3, List(4, List(5, 6))) // Output: List(1, 2, 3, 4, 5, 6)
    val result1 = flatten(nestedList1)
    println(result1)
    val nestedList2 = List(1, List(2, List(3))) // Output: List(1, 2, 3)
    val result2 = flatten(nestedList2)
    println(result2)
  }


  def flatten(lst: List[_]): List[_] = lst match {
    case Nil => Nil
    case (head: List[_]) :: tail => flatten(head) ++ flatten(tail)
    case head :: tail => head :: flatten(tail)
  }


}
