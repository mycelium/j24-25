package recfun

object Main {
  def main(args: Array[String]) {
    println("Pascal's Triangle")
    for (row <- 0 to 10) {
      for (col <- 0 to row)
        print(pascal(col, row) + " ")
      println()
    }
    println("Parentheses Balancing")
    print("'(()(()))())' balanced?: ")
    println(balance("(()(()))())".toList))
    print("'(()(()))()' balanced?: ")
    println(balance("(()(()))()".toList))

    println("Counting Change for money = 33, and coins [1, 2, 5, 10]")
    print(countChange(18, List(1, 2, 5, 10)))
  }

  /**
   * Exercise 1
   */
  def pascal(c: Int, r: Int): Int = fact(r) / (fact(c) * fact(r - c))

  private def fact(n: Int): Int = {
    if (n < 2) 1 else n * fact(n - 1)
  }

  /**
   * Exercise 2 Parentheses Balancing
   */
  def balance(chars: List[Char]): Boolean =
    chars.foldLeft(0) {
      case (0, ')') => return false
      case (x, ')') => x - 1
      case (x, '(') => x + 1
      case (x, _) => x
    } == 0

  /**
   * Exercise 3 Counting Change
   * Write a recursive function that counts how many different ways you can make
   * change for an amount, given a list of coin denominations. For example,
   * there is 1 way to give change for 5 if you have coins with denomiation
   * 2 and 3: 2+3.
   */
  def countChange(money: Int, coins: List[Int]): Int = (money, coins) match {
    case (0, _) => 1
    case (x, _) if x < 0 => 0
    case (_, Nil) => 0
    case (money, coins) => countChange(money - coins.head, coins.tail) + countChange(money, coins.tail)
  }
}
