package recfun

object Main {
  def main(args: Array[String]): Unit = {
    println("Pascal's Triangle")
    for (row <- 0 to 10) {
      for (col <- 0 to row)
        print(pascal(col, row) + " ")
      println()
    }

    println(balance(("()()((()))()()((()))").toList))
    println(balance(("()()((()))()()((()))(").toList))
    println(balance(("()()((()))()()((())))").toList))
    println(balance(("(x + 1) * 2 = 2 * x + 2").toList))
    println(balance(("(x + 1) * 2 = (2 * x + 2").toList))
  }

  /**
   * Exercise 1
   */
  def pascal(c: Int, r: Int): Int = {
    if (c == r || c == 0) 1
    else pascal(c - 1, r - 1) + pascal(c, r - 1)
  }

  /**
   * Exercise 2 Parentheses Balancing
   */
  def balance(chars: List[Char]): Boolean = {
    val r = countBrackets(0, 0, chars)
    (r.head == r.last)
  }
  def countBrackets(rightBrackets: Int, leftBrackets: Int, chars: List[Char]): List[Int] = {
    if (chars.isEmpty) List(rightBrackets, leftBrackets)
    else chars.head match {
      case ')' => countBrackets(rightBrackets + 1, leftBrackets, chars.tail)
      case '(' => countBrackets(rightBrackets, leftBrackets + 1, chars.tail)
      case _ => countBrackets(rightBrackets, leftBrackets, chars.tail)
    }

  }
  /**
   * Exercise 3 Counting Change
   * Write a recursive function that counts how many different ways you can make
   * change for an amount, given a list of coin denominations. For example,
   * there is 1 way to give change for 5 if you have coins with denomiation
   * 2 and 3: 2+3.
   */
  //  def countChange(money: Int, coins: List[Int]): Int = {
  //
  //  }
}
