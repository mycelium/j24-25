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
    println("Parentheses Balancing")
    println(balance(List('(', ')', '(')))
    println(balance(List('(', '(', ')', '(', ')', ')')))

    println("Counting Change")
    println(countChange(5, List(3, 2, 1)))
    println(countChange(5, List(2, 3)))
  }

  /**
   * Exercise 1
   */
  def pascal(c: Int, r: Int): Int = {
    def fact(a: Int): Int = if (a == 0) 1 else a * fact(a - 1)

    if (c <= r) {
      fact(r) / (fact(c) * fact(r - c))
    } else 0
  }

  /**
   * Exercise 2 Parentheses Balancing
   */
  def balance(chars: List[Char]): Boolean = {
    @annotation.tailrec
    def checkBalance(chars: List[Char], cnt: Int): Boolean = chars match {
      case Nil => cnt == 0
      case '(' :: tail => checkBalance(tail, cnt + 1)
      case ')' :: tail if cnt > 0 => checkBalance(tail, cnt - 1)
      case _ :: tail => checkBalance(tail, cnt)
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
    def countWays(money1: Int, index: Int): Int = {
      if (money1 == 0) 1

      // Если сумма меньше 0, или ни одной монеты не осталось, возвращаем 0
      else if (money1 < 0 || index >= coins.length) 0

      // Рекурсивный вызов: включаем текущую монету и исключаем её
      else countWays(money1 - coins(index), index) +
        countWays(money1, index + 1)
    }
    countWays(money, 0)
  }
}
