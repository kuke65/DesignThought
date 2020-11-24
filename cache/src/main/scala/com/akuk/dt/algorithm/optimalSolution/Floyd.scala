package com.akuk.dt.algorithm.optimalSolution

import java.util.Scanner

/**
  *
  * 到各点最短路径
  *
  */
object Floyd {


  import java.io.FileInputStream

  def main(args: Array[String]): Unit = {
    val G = Floyd.intputGragh("Floyd.txt", 6)
    val Dis = Floyd.floyd(G, 6)
    Floyd.printG(Dis, 6)
  }

  def printG(G: Array[Array[Int]], n: Int): Unit = {
    var i = 0
    while (i < n) {
      var j = 0
      while (j < n) {
        System.out.println(i + "->" + j + " " + G(i)(j))
        j += 1
      }
      i += 1
    }
  }

  def intputGragh(path: String, num: Int): Array[Array[Int]] = {
    val G = Array.ofDim[Int](num, num)
    var i = 0
    while (i < num) {
      var j = 0
      while (j < num) {
        G(i)(j) = 999
        j += 1
      }
      i += 1
    }
    val in = new Scanner(new FileInputStream(path))
    while (in.hasNext) {
      val i = in.nextInt
      val j = in.nextInt
      val weight = in.nextInt
      G(i)(j) = weight
    }
    G
  }

  def floyd(G: Array[Array[Int]], n: Int): Array[Array[Int]] = {
    val Dis = Array.ofDim[Int](n, n)
    var q = 0
    while (q < n) {
      var w = 0
      while (w < n) {
        Dis(q)(w) = G(q)(w)
        w += 1
      }
      q += 1
    }
    var k = 0
    while (k < n) {
      var i = 0
      while (i < n) {
        var j = 0
        while (j < n) {
          if (Dis(i)(j) > Dis(i)(k) + Dis(k)(j))
            Dis(i)(j) = Dis(i)(k) + Dis(k)(j)
          j += 1
        }
        i += 1
      }
      k += 1
    }
    Dis
  }

}
