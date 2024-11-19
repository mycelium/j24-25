package recfun
import common._

import scala.annotation.tailrec

object Main {
  def main(args: Array[String]) {
    println("Pascal's Triangle")
    for (row <- 0 to 10) {
      for (col <- 0 to row)
        print(pascal(col, row) + " ")
      println()
    }
    println("Exercise 2 Parentheses Balancing:")
    println("((())))((" + "   " + balance("((())))((".toList))
    println("((()))" + "   " + balance("((()))".toList))
    println("(h(ell()0)!)" + "   " + balance("(h(ell()0)!)".toList))
    println("Exercise 3 Counting Change:")
    println(countChange(25,List(1,2,5)))
    println("money = 25; coins = [1,2,5]; ways = " + countChange(25,List(1,2,5)))
  }

  /**
   * Exercise 1
   */
  def pascal(c: Int, r: Int): Int = {
    if (c == 0 || c == r) 1
    else pascal(c - 1, r - 1) + pascal(c, r - 1)
  }

  /**
   * Exercise 2 Parentheses Balancing
   */
  def balance(chars: List[Char]): Boolean = {
    @tailrec
    def balanceInner(chars: List[Char], count: Int): Boolean = {
      if (count < 0) false
      else chars match {
        case Nil => count == 0
        case '(' :: tail => balanceInner(tail, count + 1)
        case ')' :: tail => balanceInner(tail, count - 1)
        case _ :: tail => balanceInner(tail, count)
      }
    }
    balanceInner(chars, 0)
  }

  /**
   * Exercise 3 Counting Change
   * Write a recursive function that counts how many different ways you can make
   * change for an amount, given a list of coin denominations. For example,
   * there is 1 way to give change for 5 if you have coins with denomiation
   * 2 and 3: 2+3.
   */
  def countChange(money: Int, coins: List[Int]): Int = {
    if (money == 0) 1
    else if (money < 0) 0
    else if (coins.isEmpty) 0
    else {
      countChange(money, coins.tail) + countChange(money - coins.head, coins)
    }
  }

}
