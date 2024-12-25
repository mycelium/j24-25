package recfun
import scala.annotation.tailrec
//import common._

object Main {
  def main(args: Array[String]): Unit =  {
    println("Pascal's Triangle")
    for (row <- 0 to 10) {
      for (col <- 0 to row)
        print(pascal(col, row) + " ")
      println()
    }

    println("Parentheses Balancing")
    println(balance("(()())".toList))
    println(balance("((())((())((()".toList))
    println(balance("sdds()fsdsf(ds)sssds(dsds)".toList))
    println(balance("ifsfsjj(dsdj))dsjhs".toList))

    println("Counting Change")
    println(countChange(5, List(2, 3)))
    println(countChange(4, List(1, 2, 3)))
    println(countChange(10, List()))
    println(countChange(7, List(1, 3, 4)))
    println(countChange(6, List(3, 5, 10)))
  }

  /**
   * Exercise 1
   */
  def pascal(c: Int, r: Int): Int = {
    if (c == 0 || r == 0) 1;
    else
      pascal(r - 1, c) + pascal(r, c - 1)
  }

  /**
   * Exercise 2 Parentheses Balancing
   */
  def balance(chars: List[Char]): Boolean = {
    @tailrec
    def calc(chars: List[Char], count: Int): Boolean = {
      if (count < 0) false
      else if (chars.isEmpty) {
        if (count == 0) true
        else false
      }
      else chars.head match {
        case '(' => calc(chars.tail, count + 1)
        case ')' => calc(chars.tail, count - 1)
        case _ => calc(chars.tail, count)
      }
    }

    calc(chars, 0)
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
      // базовые случаи
      case (0, _) => 1
      case (money, _) if money < 0 => 0
      case (_, Nil) => 0
      // рекурсивный случай
      case (_, coin :: tail) =>
        countChange(money - coin, coins) + countChange(money, tail)
    }
  }
}
