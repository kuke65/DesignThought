package com.akuk.dt

import akka.actor.ActorSystem

object RemoteActor {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("Cache")
    val selection = system.actorSelection(s"akka.tcp://SbtRunner@127.0.0.1:5150/user/SbtActor")
    println(selection ! "123")
  }

}
