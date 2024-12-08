package recfun
//import common._

object Main {
  def main(args: Array[String]) {
    //println(getPascalValue (1 ,1))
    println("--Pascal's Triangle--")
    for (row <- 0 to 10) {
      for (col <- 0 to row) {
        print(pascal(col, row) + " ")
        //println(col,row);
      }
      println()
    }
    println()
    println("--Parentheses Balancing--")
    print("Is ()(( balanced?: ")
    println(balance(List('(', ')', '(', '(')))
    print("Is ()() balanced?: ")
    println(balance(List('(', ')', '(', ')')))
    print("Is ()a( balanced?: ")
    println(balance(List('(', ')', 'a', '(')))
    println()
    println("--Counting Change--")
    print("Change for 5 with 3,5: ")
    println(countChange(5, List(3,5)))
    print("Change for 5 with 1,2,3: ")
    println(countChange(5, List(1,2,3)))
    print("Change for 4 with 1,2: ")
    println(countChange(4, List(1,2)))
  }

  /**
   * Exercise 1
   */
  def pascal(c: Int, r: Int): Int = {
    if ( c<0 || c > r) 0
    else if (c == r || c == 0) 1
    else  pascal(c - 1, r - 1) + pascal(c , r-1)
  }

  /**
   * Exercise 2 Parentheses Balancing
   */

  def balance(chars: List[Char]): Boolean = {
    def get_balance (chairs: List[Char], count: Int): Boolean = {
      if (chairs.isEmpty) {
        count match {
          case 0 => true
          case _ => false
        }
      }
      else {
        chairs.head match {
          case '(' => get_balance(chairs.tail, count + 1)
          case ')' => get_balance(chairs.tail, count - 1)
          case _ => get_balance(chairs.tail, count)
        }
      }
    }
    get_balance(chars, 0)
  }

  /**
   //   * Exercise 3 Counting Change
   //   * Write a recursive function that counts how many different ways you can make
   //   * change for an amount, given a list of coin denominations. For example,
   //   * there is 1 way to give change for 5 if you have coins with denomiation
   //   * 2 and 3: 2+3.
   //   */
  def countChange(money: Int, coins: List[Int]): Int = {
    if (money ==0) 1
    else if (money <0 || coins.isEmpty) 0
    else countChange(money - coins.reverse.head, coins ) + countChange(money, coins.reverse.tail)
  }
}