package recfun

object Main {
  def main(args: Array[String]) {
    println("----------------Pascal's Triangle----------------")
    for (row <- 0 to 10) {
      for (col <- 0 to row)
        print(s"${pascal(col, row)} ")
      println()
    }
    println()

    println("------------Parentheses Balancing--------------")
    print("Проверяем баланс '(())())' :")
    println(balance("(())())".toList))
    
    print("Проверяем баланс '((())()())' :")
    println(balance("(()(()))())".toList))

    print("Проверяем баланс ')()(' :")
    println(balance(")()(".toList))
    println()
    println("----------------Counting Change----------------")
    print("Кол-во способов размена '4' монетами номиналом (1,2): ")
    println(countChange(4, List(1,2)))
    print("Кол-во способов размена '5' монетами номиналом (1,2,3): ")
    println(countChange(5, List(1,2,3)))
    println()
  }

  /**
   * Exercise 1
   */
  def pascal(c: Int, r: Int): Int = {
    if(r == 0 || r == 1 || c == 0 || c == r) return 1
    else return (pascal(c - 1, r - 1) + pascal(c, r - 1)) 
  }

  /**
   * Exercise 2 Parentheses Balancing
   */
  def balance(chars: List[Char]): Boolean = {
    if (chars.isEmpty) true
    else if (chars.head == ')') false
    else {
      def process(chars: List[Char], openCount: Int): Boolean = {
        if (chars.isEmpty) openCount == 0
        else if (chars.head == '(') process(chars.tail, openCount + 1)
        else if (chars.head == ')') {
          if (openCount > 0) process(chars.tail, openCount - 1)
          else false
        } else process(chars.tail, openCount)
      }
      process(chars, 0)
    }
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
    else countChange(money - coins.head, coins) + countChange(money, coins.tail)
  }
}
