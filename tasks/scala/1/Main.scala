package recfun
import common._

object Main {
  def main(args: Array[String]) {
    println("Pascal's Triangle")
    for (row <- 0 to 10) {
      for (col <- 0 to row)
        print(pascal(col, row) + " ")
      println()
    }
  }

  /**
   * Exercise 1
   */
  def pascal(c: Int, r: Int): Int = {
    if (c == 0 || c == r) 1
    else pascal(c - 1, r - 1) +
         pascal(c + 0, r - 1)
  }

  /**
   * Exercise 2 Parentheses Balancing
   */
  def balance(chars: List[Char]): Boolean = {
    @annotation.tailrec
    def rec(chars: List[Char], depth: Int): Boolean = chars match {
      case Nil                      => depth == 0
      case '(' :: tail              => rec(tail, depth + 1)
      case ')' :: tail if depth > 0 => rec(tail, depth - 1)
      case ')' :: _                 => false
      case _ :: tail                => rec(tail, depth)
    }; rec(chars, 0)
  }

  /**
   * Exercise 3 Counting Change
   * Write a recursive function that counts how many different ways you can make
   * change for an amount, given a list of coin denominations. For example,
   * there is 1 way to give change for 5 if you have coins with denomiation
   * 2 and 3: 2+3.
   */
  def countChange(money: Int, coins: List[Int]): Int = {

  }
}
