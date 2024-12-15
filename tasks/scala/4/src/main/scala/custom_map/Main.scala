package custom_map

import scala.annotation.tailrec

def customMap[A, B](list: List[A], f: A => B): List[B] =
  @tailrec
  def loop(list: List[A], acc: List[B]): List[B] = {
    list match {
      case Nil => acc
      case head :: tail => loop(list.tail, acc :+ f(head))
    }
  }
  loop(list, Nil)

object Main extends App {
  println(customMap(List(1, 2, 3), x => x * 2))
}
