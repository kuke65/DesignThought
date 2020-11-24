package com.akuk.dt.cache

import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.{ActorRef, ActorSystem, Scheduler}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import akka.util.Timeout
import com.akuk.dt.cache.RedisRoutes.{ClientCredentials, JsonSupport}
import com.akuk.dt.cache.zk.ZookeeperManager
import com.akuk.dt.cache.zk.ZookeeperManager.GetZKnode
import spray.json.JsonParser.ParsingException
import spray.json.{DefaultJsonProtocol, JsonParser, ParserInput}

import scala.concurrent.Future

object RedisRoutes {

  // 客户端凭证
  case class ClientCredentials(id: String, secret: String, scope: String, name: String, created: Long, uri: String,
                                     descr: String, ctype: Int, status: Int, detail: Map[String, String])

  trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val ClientCredentialsormat = jsonFormat10(ClientCredentials) // 有集合的情况
  }

}


class RedisRoutes()(implicit system: ActorSystem[_]) extends Directives with JsonSupport  {

  implicit private val timeout: Timeout =
    Timeout.create(system.settings.config.getDuration("cache.askTimeout"))
  implicit val schedule: Scheduler = system.scheduler

  var cache: Route =
    pathPrefix("regions") {
      concat(
        post {
          entity(as[ClientCredentials]) { data =>
            val RedisCacheRef: ActorRef[RedisCache.Command] = RedisCache.regions.get(RedisCache.EntityKey)
            val isSuccessLock: Future[Boolean] = ZookeeperManager.ref ? (reply => GetZKnode(data.id, data.name, reply))
            onSuccess(isSuccessLock) { r1 =>
              if (r1) {
                val reply: Future[RedisCache.Confirmation] = RedisCacheRef ? (reply => RedisCache.PostRedisData(data, reply))
                onSuccess(reply) {
                  case RedisCache.Accepted(r2) =>
                    complete(StatusCodes.OK -> r2)
                  case RedisCache.Rejected(reason) =>
                    complete(StatusCodes.BadRequest, reason)
                }
              } else {
                complete(StatusCodes.BadRequest, "isSuccessLock = false")
              }
            }
          }
        },
        get {
          parameters("_param") { _param => {
            try {
              val fields = JsonParser(ParserInput(_param)).asJsObject.fields
              val clientId = fields("clientId").toString(stringToSpecificType)
              val clientSecret = fields("clientSecret").toString(stringToSpecificType)
              val RedisCacheRef: ActorRef[RedisCache.Command] = RedisCache.regions.get(RedisCache.EntityKey)
              val reply: Future[RedisCache.Confirmation] = RedisCacheRef ? (reply => RedisCache.GetRedisData(clientId, clientSecret, reply))
              onSuccess(reply) {
                case RedisCache.Accepted(result) =>
                  complete(StatusCodes.OK -> result)
                case RedisCache.Rejected(reason) =>
                  complete(StatusCodes.BadRequest, reason)
              }
            } catch {
              case e: ParsingException =>
                complete(StatusCodes.BadRequest, s"格式化 json 参数异常: ${_param}")
              case e: Exception =>
                complete(StatusCodes.BadRequest, e.getMessage)
            }
          }
          }
        }
      )
    }
}

