package com.akuk.dt.cache


import java.util.concurrent.ConcurrentHashMap

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, SupervisorStrategy}
import com.akuk.dt.cache.RedisRoutes.ClientCredentials


object RedisCache {



  /**
    * 1) 成功缓存
    * 2) 未成功缓存
    */
  sealed trait Confirmation extends CborSerializable

  final case class Accepted(result: String) extends Confirmation

  final case class Rejected(reason: String) extends Confirmation

  /**
    * 1) 创建缓存数据
    * 2) 修改缓存数据
    * 3) 删除缓存数据
    * 4) 校验缓存数据
    */
  sealed trait Command extends CborSerializable

  final case class PostRedisData(clientCredentials: ClientCredentials, replyTo: ActorRef[Confirmation]) extends Command

  final case class PutRedisData(clientCredentials: ClientCredentials, replyTo: ActorRef[Confirmation]) extends Command

  final case class DeleteRedisData(index: String, esType: String, docId: String, jsonString: String, replyTo: ActorRef[Confirmation]) extends Command

  final case class GetRedisData(clientId: String, clientSecret: String, replyTo: ActorRef[Confirmation]) extends Command

  val EntityKey: String = "RedisCache"

  val regions: ConcurrentHashMap[String, ActorRef[Command]] = new ConcurrentHashMap

  def apply(): Behavior[Command] = {
    Behaviors
      .supervise[Command] {
      Behaviors.setup { ctx =>

        // 在用户读取数据需要对所有事件进行处理
        Behaviors.receiveMessage[Command] { command =>
          command match {
            case PostRedisData(clientCredentials, replyTo) =>
              try {
                replyTo ! Accepted(RedisJedisPoolClient.storeClientCredentials(clientCredentials))
              } catch {
                case e: Exception => replyTo ! Rejected(s"Redis PostCacheData return exception ${e.getMessage}")
              }
            case GetRedisData(clientId, clientSecret, replyTo) =>
              try {
                replyTo ! Accepted(RedisJedisPoolClient.validClient(clientId, clientSecret))
              } catch {
                case e: Exception => replyTo ! Rejected(s"Redis GetCacheData return exception ${e.getMessage}")
              }
          }
          Behaviors.same
        }
      }
    }.onFailure[Exception](SupervisorStrategy.restart)
  }

  def init(system: ActorSystem[_]) = {
    val ref: ActorRef[Command] = system.systemActorOf(RedisCache(), EntityKey)
    regions.putIfAbsent(EntityKey, ref)
  }


}
