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

    println("\n Counting Change")
    println(s"1) Money = 5, Coins = [1, 2, 3] : ${countChange(5, List(1, 2, 3))} ways")
    println(s"2) Money = 10, Coins = [1, 2, 5] : ${countChange(10, List(1, 2, 5))} ways")
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
    def loop(chars: List[Char], open: Int): Boolean = {
      if (open < 0) false // Закрывающая скобка без соответствующей открывающей
      else chars match {
        case Nil => open == 0
        case '(' :: tail => loop(tail, open + 1)
        case ')' :: tail => loop(tail, open - 1)
        case _ :: tail => loop(tail, open)
      }
    }
    loop(chars, 0)
  }

  /**
   * Exercise 3 Counting Change
   * Write a recursive function that counts how many different ways you can make
   * change for an amount, given a list of coin denominations. For example,
   * there is 1 way to give change for 5 if you have coins with denomiation
   * 2 and 3: 2+3.
   */
  def countChange(money: Int, coins: List[Int]): Int = {
    if (money < 0 || coins.isEmpty) {
      0
    } else if (money == 0) {
      1
    } else countChange(money, coins.tail) + countChange(money - coins.head, coins)
  }
}
