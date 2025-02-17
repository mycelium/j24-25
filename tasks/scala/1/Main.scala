package recfun

object Main {
  def main(args: Array[String]) = {
    println("Pascal's Triangle")
    for (row <- 0 to 10) {
      for (col <- 0 to row)
        print(pascal(col, row) + " ")
      println()
    }

    val balanceTests = List(
      ("()", true),
      ("(())", true),
      ("(()())", true),
      (")(", false),
      ("((())", false),
      ("())(", false),
      ("", true),
      ("(hello(world))", true)
    )

    balanceTests.foreach { case (input, expected) =>
      val result = balance(input.toList)
      println(s"Input: '$input' => $result (Expected: $expected) - ${if (result == expected) "OK" else "BROKEN"}")
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
    def loop(chars: List[Char], openCount: Int): Boolean = {
      if (chars.isEmpty) openCount == 0
      else {
        val newCount = chars.head match {
          case '(' => openCount + 1
          case ')' => openCount - 1
          case _   => openCount
        }
        if (newCount < 0) false
        else loop(chars.tail, newCount)
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
    ???
  }
}
