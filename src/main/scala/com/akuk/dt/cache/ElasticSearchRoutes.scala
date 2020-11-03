package com.akuk.dt.cache

import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.{ActorRef, ActorSystem, Scheduler}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.akuk.dt.cache.zk.ZookeeperManager
import com.akuk.dt.cache.zk.ZookeeperManager.{GetZKnode, ZKnode}
import spray.json.JsonParser.ParsingException
import spray.json.{JsonParser, ParserInput}

import scala.concurrent.Future

object ElasticSearchRoutes {
  final case class PostIndex(index: String, esType: String, docId: String, jsonString: String)
  final case class PutIndex(index: String, esType: String, docId: String, jsonString: String)
  final case class GetIndex(index: String, esType: String, docId: String, jsonString: String)

}

class ElasticSearchRoutes()(implicit system: ActorSystem[_]) {

  implicit private val timeout: Timeout =
    Timeout.create(system.settings.config.getDuration("cache.askTimeout"))
  implicit val schedule: Scheduler = system.scheduler

  import ElasticSearchRoutes._
  import JsonFormats._
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import akka.http.scaladsl.server.Directives._

  var cache: Route =
    pathPrefix("elasticSearch") {
      pathPrefix("regions") {
        concat(
          post {
            entity(as[PostIndex]) { data =>
              val elasticSearchCacheRef: ActorRef[ElasticSearchCache.Command] = ElasticSearchCache.regions.get(ElasticSearchCache.EntityKey)
              val isSuccessLock: Future[Boolean] = ZookeeperManager.ref ? (reply =>  GetZKnode(data.index, data.esType, reply))
              onSuccess(isSuccessLock) { result =>
                if(result) {
                  val reply: Future[ElasticSearchCache.Confirmation] = elasticSearchCacheRef ? (reply => ElasticSearchCache.PostIndexData(data.index, data.esType, data.docId, data.jsonString, reply))
                  onSuccess(reply) {
                    case ElasticSearchCache.Accepted(result) =>
                      complete(StatusCodes.OK -> result)
                    case ElasticSearchCache.Rejected(reason) =>
                      complete(StatusCodes.BadRequest, reason)
                  }
                }else {
                  complete(StatusCodes.BadRequest, "isSuccessLock = false")
                }
              }
            }
          },
          get {
            parameters("_param") { _param => {
              try {
                val jsonObject = JsonParser(ParserInput(_param))
                val index = jsonObject.asJsObject.fields("index").toString(stringToSpecificType)
                val esType = jsonObject.asJsObject.fields("esType").toString(stringToSpecificType)
                val docId = jsonObject.asJsObject.fields("docId").toString(stringToSpecificType)
                val jsonString = jsonObject.asJsObject.fields("jsonString").toString(stringToSpecificType)
                val elasticSearchCacheRef: ActorRef[ElasticSearchCache.Command] = ElasticSearchCache.regions.get(ElasticSearchCache.EntityKey)
                val reply: Future[ElasticSearchCache.Confirmation] = elasticSearchCacheRef ? (reply => ElasticSearchCache.GetIndexData(index, esType, docId, jsonString, reply))
                onSuccess(reply) {
                  case ElasticSearchCache.Accepted(result) =>
                    complete(StatusCodes.OK -> result)
                  case ElasticSearchCache.Rejected(reason) =>
                    complete(StatusCodes.BadRequest, reason)
                }
              } catch {
                case e: ParsingException =>
                  complete(StatusCodes.BadRequest, "格式化 json 参数异常")
                case e: Exception =>
                  complete(StatusCodes.BadRequest, e.getMessage)
              }
            }
            }
          }
        )
      }
    }

}

object JsonFormats {

  import spray.json.DefaultJsonProtocol._
  import spray.json.RootJsonFormat

  implicit val postIndexDataFormat: RootJsonFormat[ElasticSearchRoutes.PostIndex] =
    jsonFormat4(ElasticSearchRoutes.PostIndex)
  implicit val putIndexDataFormat: RootJsonFormat[ElasticSearchRoutes.PutIndex] =
    jsonFormat4(ElasticSearchRoutes.PutIndex)
  implicit val getIndexDataFormat: RootJsonFormat[ElasticSearchRoutes.GetIndex] =
    jsonFormat4(ElasticSearchRoutes.GetIndex)

}
