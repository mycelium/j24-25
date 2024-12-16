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
   * Exercise 1: Pascal's Triangle
   */
  def pascal(c: Int, r: Int): Int = {
    if (c == 0 || c == r) 1
    else pascal(c - 1, r - 1) + pascal(c, r - 1)
  }

  /**
   * Exercise 2: Parentheses Balancing
   * Checks if parentheses in the given list are balanced.
   */
  def balance(chars: List[Char]): Boolean = {
    def balanceHelper(chars: List[Char], count: Int): Boolean = {
      if (count < 0) false // If there are more closing than opening brackets
      else if (chars.isEmpty) count == 0 // If the list is empty, check if all opened brackets are closed
      else {
        chars.head match {
          case '(' => balanceHelper(chars.tail, count + 1) // Increment count for opening bracket
          case ')' => balanceHelper(chars.tail, count - 1) // Decrement count for closing bracket
          case _ => balanceHelper(chars.tail, count) // Ignore other characters
        }
      }
    }
    balanceHelper(chars, 0)
  }

  /**
   * Exercise 3: Counting Change
   * Finds the number of ways to make change for a given amount using the available coins.
   */
  def countChange(money: Int, coins: List[Int]): Int = {
    if (money == 0) 1 // If money is zero, there is one way — no coins needed
    else if (money < 0 || coins.isEmpty) 0 // If money is negative or no coins left, no valid combinations
    else countChange(money - coins.head, coins) + countChange(money, coins.tail) // Either take the coin or skip it
  }
}
