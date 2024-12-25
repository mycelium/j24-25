package recfun

object Main {

  def main(args: Array[String]) = {
    println("Pascal's Triangle")
    for (row <- 0.to(10)) {
      for (col <- 0.to(row))
        print(pascal(col, row) + " ")
      println()
    }

    println("\nParentheses Balancing")
    val testString1 = "(a + b) * (b - a)".toList
    val testString2 = "((a + b) * (b - a)".toList
    println(testString1.mkString)
    println("result: " + balance(testString1))
    println(testString2.mkString)
    println("result: " + balance(testString2))

    println("\nCounting Change")
    val summ = 5
    val coins1 = List(2, 3)
    val coins2 = List(1, 2, 3)
    println(summ + " и [" + coins1.mkString(", ") + "]")
    println("result: " + countChange(summ, coins1))
    println(summ + " и [" + coins2.mkString(", ") + "]")
    println("result: " + countChange(summ, coins2))
  }

  /**
   * Exercise 1
   */
  def pascal(c: Int, r: Int): Int = {
      if (c==0 || c == r) 1
      else pascal(c-1, r-1) + pascal(c, r-1)
  }

  /**
   * Exercise 2 Parentheses Balancing
   */
   def balance(chars: List[Char]): Boolean = {  
    def brackets(chars: List[Char], count: Int): Boolean = {
      if (count < 0) false
      else if (chars.isEmpty) count == 0
      else chars.head match {
        case '(' => brackets(chars.tail, count + 1)
        case ')' => brackets(chars.tail, count - 1)
        case _ => brackets(chars.tail, count)
      }
    }
    brackets(chars, 0)
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
    else countChange(money, coins.tail) + countChange(money - coins.head, coins)
   }
}
