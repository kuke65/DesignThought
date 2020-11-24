package com.akuk.dt.algorithm.arithmetic

import java.text.{DecimalFormat, NumberFormat}


/**
  *
  * 百分比
  * 描述: ##.00[%]
  *
  */
object Percentage {

  /**
    *
    * @param y 被除数
    * @param z 除数
    * @return
    */
  def apply(y: Any, z: Any): String = {
    var m: Double = 0D
    var n: Double = 0D
    y match {
      case a: Byte => m = a.toDouble
      case a: Char => m = a.toDouble
      case a: Short => m = a.toDouble
      case a: Int => m = a.toDouble
      case a: Float => m = a.toDouble
      case a: Double => m = a
    }
    z match {
      case a: Byte => n = a.toDouble
      case a: Char => n = a.toDouble
      case a: Short => n = a.toDouble
      case a: Int => n = a.toDouble
      case a: Float => n = a.toDouble
      case a: Double => n = a
    }
    apply(m, n, symbol = false)
  }

  /**
    *
    * @param m      被除数
    * @param n      除数
    * @param symbol 是否带单位
    * @return
    */
  def apply(m: Double, n: Double, symbol: Boolean): String = {
    val df1 = new DecimalFormat("##.00%")
    val result = df1.format(m / n)
    if (!symbol) {
      return result.substring(0, result.length - 1)
    }
    result
  }

  def main(args: Array[String]): Unit = {
    println(Percentage(29, 59))
    println(Percentage(29, 59, symbol = true))
  }

}
