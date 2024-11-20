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
    println("Balance Parentheses")
    println("() is balanced: " + balance("()".toList))
    println("(()) is balanced: " + balance("(())".toList))
    println("(())) is balanced: " + balance("(()))".toList))
    println("(()(())()) is balanced: " + balance("(()(())())".toList))
    println(")( is balanced: " + balance(")(".toList))
    println("abc(d)e)f is balanced: " + balance("abc(d)e)f".toList))
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
    def checkBalance(chars: List[Char], openCount: Int): Boolean = {
      if (chars.isEmpty) openCount == 0
      else if (openCount < 0) false
      else chars.head match {
        case '(' => checkBalance(chars.tail, openCount + 1)
        case ')' => checkBalance(chars.tail, openCount - 1)
        case _   => checkBalance(chars.tail, openCount)
      }
    }
    checkBalance(chars, 0)
  }

  /**
   * Exercise 3 Counting Change
   * Write a recursive function that counts how many different ways you can make
   * change for an amount, given a list of coin denominations. For example,
   * there is 1 way to give change for 5 if you have coins with denomiation
   * 2 and 3: 2+3.
   */
  def countChange(money: Int, coins: List[Int]): Int = {
    0
  }
}
