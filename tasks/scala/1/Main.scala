object Main {
  def main(args: Array[String]): Unit = {
    println("Pascal's Triangle")
    for (row <- 0 to 10) {
      for (col <- 0 to row)
        print(pascal(col, row) + " ")
      println()
    }

    println("\nParentheses Balancing")
    val parenthesesExpr1 = "()((()())())"
    println(s"Is '$parenthesesExpr1' balanced?: ${balance(parenthesesExpr1.toList)}")
    val parenthesesExpr2 = "()((()())())(()("
    println(s"Is '$parenthesesExpr2' balanced?: ${balance(parenthesesExpr2.toList)}")
  }

  /**
   * Exercise 1
   */
  def pascal(c: Int, r: Int): Int = {
    factorial(r) / (factorial(c) * factorial(r - c))
  }

  private def factorial(num: Int): Int = {
    if (num == 0) 1
    else num * factorial(num - 1)
  }

  /**
   * Exercise 2 Parentheses Balancing
   */
  def balance(chars: List[Char]): Boolean = {
    val result = chars.foldLeft(0) {
      case (x, ')') if x <= 0 => -1
      case (x, ')') => x - 1
      case (x, '(') => x + 1
      case (x, _) => x
    }
    result == 0
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
