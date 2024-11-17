package recfun

object Main {
  def main(args: Array[String]) {
    println("Pascal's Triangle")
    for (row <- 0 to 10) {
      for (col <- 0 to row)
        print(pascal(col, row) + " ")
      println()
    }
    println('\n' + "Results of the balance function")
    println("for \"(()(()))()\": must be - true, result - " ++ balance("(()(()))()".toList).toString)
    println("for \"(()))\": must be - false, result - " ++ balance("(()))".toList).toString)
    println("for \"(((1)aa))\": must be - true, result - " ++ balance("(((1)aa))".toList).toString)
  }

  /**
   * Exercise 1
   */
  def pascal(c: Int, r: Int): Int = {
     fact(r)/(fact(r-c) * fact(c)) //Биномиальный коэффициент
  }

  def fact (n:Int) : Int ={
    n match {
      case 0 | 1 => 1
      case n => n*fact(n-1)
    }
  }

  /**
   * Exercise 2 Parentheses Balancing
   */
  def balance(chars: List[Char]): Boolean = {
    @annotation.tailrec
    def scopes(chars: List[Char], open: Int = 0): Boolean = chars match {
      case Nil => open == 0 //Если весь список обработан, смотрим, равен ли open нулю
      case _ if open < 0 => false //Как только выходим в минус по открытым скобкам - баланс нарушен
      case '(' :: tail => scopes(tail, open+1) //Если скобка открытая, то увеличиваем счетчик
      case ')' :: tail => scopes(tail, open-1) //Если скобка закрытая, то уменьшаем счетчик
      case _ :: tail => scopes(tail, open)  //На случай, если в строке не только скобки
    }
    scopes(chars, 0)
  }

  /**
   * Exercise 3 Counting Change
   * Write a recursive function that counts how many different ways you can make
   * change for an amount, given a list of coin denominations. For example,
   * there is 1 way to give change for 5 if you have coins with denomiation
   * 2 and 3: 2+3.
   */
  def countChange(money: Int, coins: List[Int]): Int = {
1
  }
}
