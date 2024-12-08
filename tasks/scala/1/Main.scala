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

    println("\nParentheses Balancing")
    val testCases = List(
      "()" -> true,
      "(())" -> true,
      "(()" -> false,
      "())" -> false,
      "((()))" -> true,
      "(()())" -> true,
      "())(" -> false
    )
    
    for ((input, expected) <- testCases) {
      val result = balance(input.toList)
      println(s"balance($input) = $result (expected: $expected)")
    }

    println("\nCounting Change")
    val changeTestCases = List(
      (5, List(2, 3)) -> 1,
      (4, List(1, 2, 3)) -> 4,
      (0, List(1, 2, 3)) -> 1,
      (3, List(2)) -> 0,
      (10, List(5, 2, 3)) -> 5
    )
    
    for (((money, coins), expected) <- changeTestCases) {
      val result = countChange(money, coins)
      println(s"countChange($money, ${coins.mkString(", ")}) = $result (expected: $expected)")
    }
  
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
  def helper(chars: List[Char], count: Int): Boolean = {
    chars match {
      case Nil => count == 0 
      case '(' :: tail => helper(tail, count + 1) 
      case ')' :: tail => 
        if (count > 0) helper(tail, count - 1) 
        else false 
      case _ :: tail => helper(tail, count)
    }
  }
  helper(chars, 0)
  }

  /**
   * Exercise 3 Counting Change
   * Write a recursive function that counts how many different ways you can make
   * change for an amount, given a list of coin denominations. For example,
   * there is 1 way to give change for 5 if you have coins with denomiation
   * 2 and 3: 2+3.
   */
  def countChange(money: Int, coins: List[Int]): Int = {
    (money, coins) match {
      case (0, _) => 1
      case (m, _) if m < 0 => 0 
      case (_, Nil) => 0 
      case _ => 
        countChange(money - coins.head, coins) + countChange(money, coins.tail)
    }
  }
}
