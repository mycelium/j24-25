package recfun

object Main {
  def main(args: Array[String]): Unit = {
    println("Pascal's Triangle")
    for (row <- 0 to 10) {
      for (col <- 0 to row)
        print(pascal(col, row) + " ")
      println()
    }

    println(balance(")(".toList)) // false
    println(balance("a(b)c".toList)) // true
    println(balance("()()(()())".toList)) // true
    println(balance("()()((())".toList)) // false

    println(countChange(12, List(2, 3, 5, 10))) // 6
  }

  def pascal(c: Int, r: Int): Int = {
    if (c == 0 || c == r) 1
    else pascal(c - 1, r - 1) + pascal(c, r - 1)
  }

  def balance(chars: List[Char]): Boolean = {
    def inner(chars: List[Char], open: Int): Boolean = {
      if (chars.isEmpty) open == 0
      else if (open < 0) false
      else chars.head match {
        case '(' => inner(chars.tail, open + 1)
        case ')' => inner(chars.tail, open - 1)
        case _ => inner(chars.tail, open)
      }
    }

    inner(chars, 0)
  }

  def countChange(money: Int, coins: List[Int]): Int = {
    def countRecursive(money: Int, coins: List[Int]): Int = {
      if (money == 0) 1
      else if (money < 0 || coins.isEmpty) 0
      else countRecursive(money - coins.head, coins) + countRecursive(money, coins.tail)
    }

    countRecursive(money, coins.sorted)
  }
}
