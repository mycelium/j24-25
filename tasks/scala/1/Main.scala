package recfun

object Main {

  def main(args: Array[String]) = {
    println("Pascal's Triangle")
    for (row <- 0.to(10)) {
      for (col <- 0.to(row))
        print(pascal(col, row) + " ")
      println()
    }
    println()

    println("Parentheses Balancing")
    val str1 = "func(x,y)".toList
    val str2 = "func(g(x,y)".toList
    val str3 = "x*(y+z)+((x-y)*(x+y)".toList
    val str4 = "x*(y+z)+(x-y)*(x+y)".toList
    println(str1.mkString + " -> " + balance(str1))
    println(str2.mkString + " -> " + balance(str2))
    println(str3.mkString + " -> " + balance(str3))
    println(str4.mkString + " -> " + balance(str4))

    println("\nCounting Change")
    val money1 = 5
    val coins11 = List(2, 3)
    val coins12 = List(1, 2, 3)
    val money2 = 7
    val coins21 = List(3, 4)
    val coins22 = List(2, 3, 4)
    val coins23 = List(1, 2, 3, 4)
    println(money1 + " and [" + coins11.mkString(", ") + "]" + " -> " + countChange(money1, coins11))
    println(money1 + " and [" + coins12.mkString(", ") + "]" + " -> " + countChange(money1, coins12))
    println(money2 + " and [" + coins21.mkString(", ") + "]" + " -> " + countChange(money2, coins21))
    println(money2 + " and [" + coins22.mkString(", ") + "]" + " -> " + countChange(money2, coins22))
    println(money2 + " and [" + coins23.mkString(", ") + "]" + " -> " + countChange(money2, coins23))
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
    chars.foldLeft(0) {
      case (count, '(') => count + 1
      case (count, ')') => if (count > 0) count - 1 else return false
      case (count, _) => count
    } == 0
  }
  /**
   * Exercise 3 Counting Change
   * Write a recursive function that counts how many different ways you can make
   * change for an amount, given a list of coin denominations. For example,
   * there is 1 way to give change for 5 if you have coins with denomiation
   * 2 and 3: 2+3.
   */
  def countChange(money: Int, coins: List[Int]): Int = {
    def findCombinations(remainingAmount: Int, availableCoins: List[Int]): Int = {
      if (remainingAmount < 0) 0
      else if (availableCoins.isEmpty) if (remainingAmount == 0) 1 else 0
      else findCombinations(remainingAmount, availableCoins.tail) +
        findCombinations(remainingAmount - availableCoins.head, availableCoins)
    }
    findCombinations(money, coins)
  }

}

