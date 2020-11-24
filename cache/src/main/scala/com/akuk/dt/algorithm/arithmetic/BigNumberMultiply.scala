package com.akuk.dt.algorithm.arithmetic

/**
  *
  * 大数相乘
  *
  * 1）位数越长越慢
  * 2）num1 和 num2 最大位数为 Int.MaxValue
  * 3）大数运算尽量使用 BigInt 效率更高
  * 4）一定条件下，5万位(不固定)以下大数运算+IO效率 高于 BigInt运算+IO
  * 5) 超过5万位运算，算法效率逐渐变低
  * @author Shuheng.Zhang
  *
  */
object BigNumberMultiply {

  private[this] final def multiply(num1: Array[Char], num2: Array[Char]): Array[Int] = {
    val len = num1.length + num2.length
    val result = new Array[Int](len)
    r1(result, 0, num1, num2)
    r2(result, len - 1)

    /*var i = 0
    while (i < num1.length) {
      var j = 0
      while (j < num2.length) {
        result(i + j + 1) += (num1(i) - 48)  * (num2(j) - 48)
        j += 1
      }
      i += 1
    }


    var k = result.length - 1
    while (k > 0) {
      result(k - 1) += result(k) / 10
      result(k) %= 10
      k -= 1
    }*/
    result
  }

  /**
    *
    *
    * 递归代码看起来比较美观
    *
    *
    */
  private[this] final def r0(result: Array[Int], i: Int, j: Int, num1: Array[Char], num2: Array[Char]): Unit = {
    if(j < num2.length) {
      result(i + j + 1) += (num1(i) - 48)  * (num2(j) - 48)
      r0(result, i, j + 1, num1, num2)
    }
  }

  private[this] final def r1(result: Array[Int], i: Int, num1: Array[Char], num2: Array[Char]): Unit = {
    if(i < num1.length) {
      r0(result, i, 0, num1, num2)
      r1(result, i + 1, num1, num2)
    }
  }

  private[this] final def r2(result: Array[Int], k: Int): Unit = {
    if(k > 0) {
      result(k - 1) +=  result(k) / 10
      result(k) %= 10
      r2(result, k - 1)
    }
  }

  def apply(num1: String, num2: String): String = {
    val builder = new StringBuilder()
    multiply(
      Array[Char](num1.toCharArray: _*),
      Array[Char](num2.toCharArray: _*)).map(builder.append)
    if(builder(0) == 48) {
      builder.deleteCharAt(0)
    }
    builder.toString()
  }


  def main(args: Array[String]): Unit = {
    var i = 1
    val c1 = System.currentTimeMillis()
    var t = "1"
    while (i < 100000) {
      t  = apply(i.toString, t)
      i += 1
    }
    val c2 = System.currentTimeMillis()
    println(c2 - c1)
    println(t.length)

    //println(apply("121", "121"))
  }


}
