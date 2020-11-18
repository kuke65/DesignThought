package com.akuk.dt.cache

import java.util.concurrent.ConcurrentHashMap

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}

/**
 * ElasticSearchCache
 *
 * @author Shuheng.Zhang
 * @date 2020-10-29
 */
object ElasticSearchCache {

  /**
   * 1) 成功缓存
   * 2) 未成功缓存
   */
  sealed trait Confirmation extends CborSerializable

  final case class Accepted(result: String) extends Confirmation

  final case class Rejected(reason: String) extends Confirmation

  /**
   * 1) 创建索引数据
   * 2) 修改索引数据
   * 3) 删除索引数据
   * 4) 获取索引数据
   */
  sealed trait Command extends CborSerializable

  final case class PostIndexData(index: String, esType: String, docId:String, jsonString: String, replyTo: ActorRef[Confirmation]) extends Command

  final case class PutIndexData(index: String, esType: String, docId:String, jsonString: String, replyTo: ActorRef[Confirmation]) extends Command

  final case class DeleteIndexData(index: String, esType: String, docId:String, jsonString: String, replyTo: ActorRef[Confirmation]) extends Command

  final case class GetIndexData(index: String, esType: String, docId:String, jsonString: String, replyTo: ActorRef[Confirmation]) extends Command

  val EntityKey : String = "ElasticSearchCache"

  val regions: ConcurrentHashMap[String, ActorRef[Command]] = new ConcurrentHashMap

  def apply(): Behavior[Command] = {
    // 在用户读取数据需要对所有事件进行处理
    Behaviors.receive { (ctx, command) =>
      command match {
        case PostIndexData(index, esType, docId, jsonString, replyTo) =>
          try {
            replyTo ! Accepted(ElasticSearchLowLevelRestClient.indexDataPost(index, esType, docId, jsonString))
          } catch {
            case e: Exception => replyTo ! Rejected(s"ES indexDataPost return exception: ${e.getMessage}")
          }
        case GetIndexData(index, esType, docId, jsonString, replyTo) =>
          try {
            replyTo ! Accepted(ElasticSearchLowLevelRestClient.indexDataGet(index, esType, docId, jsonString))
          } catch {
            case e: Exception => replyTo ! Rejected(s"ES indexDataGet return exception: ${e.getMessage}")
          }

      }
      Behaviors.same
    }
  }

  def init(system: ActorSystem[_]) = {
    val ref: ActorRef[Command] = system.systemActorOf(ElasticSearchCache(), EntityKey)
    regions.putIfAbsent(EntityKey, ref)
  }
}
