package com.akuk.dt.cache.zk

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}


object ZookeeperManager {

  sealed trait Command

  case class GetZKnode(nodeName: String, itemName: String, replyTo: ActorRef[Boolean]) extends Command

  case class ZKnode(nodeName: String, itemName: String) extends Command

  var clientActorLock: ClientActorLock = _
  var ref: ActorRef[GetZKnode] = _

  def apply(): Behavior[GetZKnode] = {
    Behaviors.setup[GetZKnode] { context => {
      Behaviors.receiveMessage[GetZKnode] { getZKnode =>
        getZKnode.replyTo ! clientActorLock.lock(getZKnode.nodeName, getZKnode.itemName)
        /*getZKnode match {
          case ZKnode(replyTo) =>
        }*/
        Behaviors.same
      }
    }}
  }

  def init(system: ActorSystem[_]) = {
    ref  = system.systemActorOf(ZookeeperManager(), "ZookeeperManager")
    clientActorLock = new ClientActorLock(system.settings.config.getString("zookeeper.address"))
  }

}
