package nqueens

import scala.annotation.tailrec

@tailrec
def isSafe(queenK: (Int, Int), tuples: List[(Int, Int)]): Boolean =
  tuples match {
    case Nil => true
    case ::(head, tail) => !(queenK._1 eq head._1) && !(queenK._2 eq head._2)
      && !(Math.abs(queenK._1 - head._1) eq Math.abs(queenK._2 - head._2)) && isSafe(queenK, tail)
  }

def queens(n: Int): List[List[(Int, Int)]] = {
  def placeQueens(k: Int): List[List[(Int, Int)]] =
    if (k == 0)
      List(List())
    else
      for {
        queens <- placeQueens(k - 1)
        column <- 1 to n
        queen = (k, column)
        if isSafe(queen, queens)
      } yield queen :: queens

  placeQueens(n)
}

object Main extends App {
  val n = 8
  println(queens(n))
}
