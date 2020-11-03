package com.akuk.dt.cache

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import com.akuk.dt.cache.zk.ZookeeperManager
import com.typesafe.config.{Config, ConfigFactory}

object Main {

  def main(args: Array[String]): Unit = {
    args.headOption match {

      case Some(portString) if portString.matches("""\d+""") =>
        val port = portString.toInt
        val httpPort = ("80" + portString.takeRight(2)).toInt
        startNode(port, httpPort)
    }
  }

  def startNode(port: Int, httpPort: Int): Unit = {
    val system = ActorSystem[Nothing](Guardian(), "Cache", config(port, httpPort))
  }

  def config(port: Int, httpPort: Int): Config =
    ConfigFactory.parseString(s"""
      akka.remote.artery.canonical.port = $port
      cache.http.port = $httpPort
       """).withFallback(ConfigFactory.load())

}

object Guardian {

  def apply(): Behavior[Nothing] = {
    Behaviors.setup[Nothing] { context =>
      val system = context.system
      val httpPort = context.system.settings.config.getInt("cache.http.port")

      ZookeeperManager.init(system)
      ElasticSearchCache.init(system)
      val routes = new ElasticSearchRoutes()(context.system)
      new CacheServer(routes.cache, httpPort, context.system).start()
      Behaviors.empty

    }

  }
}
