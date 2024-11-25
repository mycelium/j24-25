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

    println("______balance___")
    print("'(()(()))())()()())))': ")
    println(balance("(()(()))())()()())))".toList))
    print("'(()()())': ")
    println(balance("(()()())".toList))

    println("money = 15, coins [1, 3, 2]")
    println(countChange(15, List(1, 3, 2)))
    println("money = 10, coins [1, 3, 2]")
    println(countChange(10, List(1, 3, 2)))
    println("money = 10, coins [1, 3, 2]")
    println(countChange(5, List(1, 3, 2)))
  }

  /**
   * Exercise 1
   */
  def pascal(c: Int, r: Int): Int = {
    if (c < 0 || r < 0 || c > r) 0 // Некорректные входные данные
    else if (c == 0 || c == r) 1 // Базовые случаи: крайние элементы
    else pascal(c - 1, r - 1) + pascal(c, r - 1)
  }

  /**
   * Exercise 2 Parentheses Balancing
   */

  def balance(chars: List[Char]): Boolean = {
    def count_balance (chs: List[Char], count: Int): Boolean = {
      if (chs.isEmpty) {
        if (count == 0) true
        else false
      }
      else if (chs.head == '(')  count_balance(chs.tail, count + 1)
      else if (chs.head == ')')  count_balance(chs.tail, count - 1)
      else { count_balance(chs.tail, count) }
    }
    count_balance(chars, 0)
  }
  /**
   * Exercise 3 Counting Change
   * Write a recursive function that counts how many different ways you can make
   * change for an amount, given a list of coin denominations. For example,
   * there is 1 way to give change for 5 if you have coins with denomiation
   * 2 and 3: 2+3.
   */
  def countChange(money: Int, coins: List[Int]): Int = {
    if (money < 0 || coins.isEmpty) 0
    else {
      if (money == 0) 1
      else {
        val firstCoin = coins.head
        val remainingCoins = coins.tail
        countChange(money - firstCoin, coins) + countChange(money, remainingCoins)
      }
    }
  }
}
