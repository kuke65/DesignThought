package com.akuk.dt.cache

import java.util.concurrent.ConcurrentHashMap
import akka.http.scaladsl
import akka.http.scaladsl.server.Directives._

object TopLevelRoute {

  val routeMap: ConcurrentHashMap[String, scaladsl.server.Route] = new ConcurrentHashMap

  lazy val topLevelRoute: scaladsl.server.Route =
    concat(
      pathPrefix("elasticSearch")(routeMap.get("elasticSearch")),
      pathPrefix("redis")(routeMap.get("redis")),
    )

}

