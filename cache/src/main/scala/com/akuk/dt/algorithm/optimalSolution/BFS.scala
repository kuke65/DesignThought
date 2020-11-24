package com.akuk.dt.algorithm.optimalSolution

import java.util

/**
  *
  * 广度优先遍历(BFS)计算最短路径的方法
  *
  */
object BFS {

  trait Algorithm {
    /**
      * 执行算法
      */
    def perform(g: Graph, sourceVertex: String): Unit

    /**
      * 得到路径
      */
    def getPath: util.Map[String, String]
  }

  class Graph {

    import java.util
    // 图的起点// 图的起点

    private var firstVertax: String = null
    // 邻接表
    private val adj: util.Map[String, util.List[String]] = new util.HashMap()
    // 遍历算法
    private var algorithm: Algorithm = null

    def this(algorithm: Algorithm) {
      this()
      this.algorithm = algorithm
    }

    /**
      * 执行算法
      */
    def done(): Unit = {
      algorithm.perform(this, firstVertax)
    }

    /**
      * 得到从起点到{@code vertex}点的最短路径
      *
      * @param vertex
      * @return
      */
    def findPathTo(vertex: String): util.Stack[String] = {
      val stack = new util.Stack[String]
      stack.add(vertex)
      val path = algorithm.getPath
      var location: String = path.get(vertex)
      while (!location.equals(firstVertax)) {
        stack.push(location)
        location = path.get(location)
      }
      stack.push(firstVertax)
      stack
    }

    /**
      * 添加一条边
      */
    def addEdge(fromVertex: String, toVertex: String): Unit = {
      if (firstVertax == null)
        firstVertax = fromVertex
      adj.get(fromVertex).add(toVertex)
      adj.get(toVertex).add(fromVertex)
    }

    /**
      * 添加一个顶点
      */
    def addVertex(vertex: String): Unit = {
      adj.put(vertex, new util.ArrayList[String])
    }

    def getAdj: util.Map[String, util.List[String]] = adj
  }


  class BroadFristSearchAlgorithm extends Algorithm {

    import java.util
    // 保存已经访问过的地点// 保存已经访问过的地点

    private var visitedVertex: util.List[String] = _
    // 保存最短路径
    private var path: util.Map[String, String] = _

    def perform(g: Graph, sourceVertex: String): Unit = {
      if (null == visitedVertex) visitedVertex = new util.ArrayList[String]
      if (null == path) path = new util.HashMap[String, String]
      BFS(g, sourceVertex)
    }

    def getPath: util.Map[String, String] = path

    private def BFS(g: Graph, sourceVertex: String): Unit = {
      val queue = new util.LinkedList[String]
      // 标记起点
      visitedVertex.add(sourceVertex)
      // 起点入列
      queue.add(sourceVertex)
      while (!queue.isEmpty) {
        val ver = queue.poll
        val toBeVisitedVertex = g.getAdj.get(ver)
        toBeVisitedVertex.forEach(v =>
          if (!visitedVertex.contains(v)) {
            visitedVertex.add(v)
            path.put(v, ver)
            queue.add(v)
          }
        )
      }
    }
  }

  def main(args: Array[String]): Unit = {
    val vertex = Array("North Gate", "South Gate", "Classroom", "Square", "Toilet", "Canteen")
    val edges = Array(Array("North Gate", "Classroom"),
      Array("North Gate", "Square"),
      Array("Classroom", "Toilet"),
      Array("Square", "Toilet"),
      Array("Square", "Canteen"),
      Array("Toilet", "South Gate"),
      Array("Toilet", "South Gate"))
    val g = new Graph(new BroadFristSearchAlgorithm)
    vertex.foreach(g.addVertex)
    edges.foreach(arr => g.addEdge(arr(0),arr(1)))
    g.done()
    val result = g.findPathTo("Canteen")
    System.out.println("BFS: From [North Gate] to [Canteen]:")
    while (!result.isEmpty)
      System.out.println(result.pop)
  }

}
