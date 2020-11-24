package com.akuk.dt.algorithm.arithmetic

/**
  *
  * 阶乘测试
  * 因子： BigInt 和 BigNumberMultiply
  *
  */
object BigNumberFactorial {

  def main(args: Array[String]): Unit = {
    var i = 1
    var t: BigInt = 1
    val c1 = System.currentTimeMillis()
    while (i <= 10000) {
      t  = t * i
      i += 1
    }
    val c2 = System.currentTimeMillis()
    println(s"阶乘长度： ${t.toString().length}")
    //println(s"阶乘结果： ${t}")
    println(c2 - c1)

    val c3 = System.currentTimeMillis()
    t = t * t
    val s = t.toString()
    //val s = BigNumberMultiply(t.toString(), t.toString())
    val c4 = System.currentTimeMillis()
    println(s"运算后长度： ${s.length}")
    println(c4 -  c3)
  }

}
