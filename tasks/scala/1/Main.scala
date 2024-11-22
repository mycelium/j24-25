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
    println("Function balance")
    println("() is balanced: " + balance("()".toList))       // true
    println("(() is balanced: " + balance("(()".toList))      // false
    println("()) is balanced: " + balance("())".toList))      // false
    println("(if (a > b) (a/b) v (b > a) (b/a)) is balanced: " +balance("(if (a > b) (a/b) v (b > a) (b/a))".toList))  // true

    println("Function countChange")
    println(countChange(6, List(1, 2, 3)))
    println(countChange(8, List(5, 1, 3)))
    println(countChange(4, List(1, 2, 3)))

  }

  /**
   * Exercise 1
   */
  def pascal(c: Int, r: Int): Int = {
    def checkPascal(current: Int, acc: Int): Int = {
      if (current == c) acc
      else checkPascal(current + 1, acc * (r - current) / (current + 1))
    }

    checkPascal(0, 1)

  }

  /**
   * Exercise 2 Parentheses Balancing
   */
  def balance(chars: List[Char]): Boolean = {
    def checkBalance(chars: List[Char], openCount: Int): Boolean = {
      if (chars.isEmpty) openCount == 0 // Баланс должен быть равен 0 в конце
      else if (openCount < 0) false // Если счетчик стал отрицательным, баланс нарушен
      else {
        if (chars.head == '(') checkBalance(chars.tail, openCount + 1)
        else if (chars.head == ')') checkBalance(chars.tail, openCount - 1)
        else checkBalance(chars.tail, openCount)
        // Рекурсивный вызов для оставшихся символов
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
    if (money == 0) 1
    else if (money < 0 || coins.isEmpty) 0
    else {
      countChange(money - coins.head, coins) + countChange(money, coins.tail)
    }
  }
}
