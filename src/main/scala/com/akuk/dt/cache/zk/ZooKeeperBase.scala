package com.akuk.dt.cache.zk

import java.util.concurrent.{CountDownLatch, TimeUnit}

import org.apache.zookeeper.Watcher.Event
import org.apache.zookeeper._
import org.slf4j.LoggerFactory

/**
 * 实现Watcher功能 简单的抽象类
 */
class ZooKeeperBase extends Watcher {
  private[zk] val log = LoggerFactory.getLogger(getClass)
  private[zk] var zooKeeper: ZooKeeper = null
  private[zk] val connecting = new CountDownLatch(1)
  private[zk] val syncRootNodeClass = Integer.valueOf(1)

  def this(address: String) {
    this()
    zooKeeper = new ZooKeeper(address, 30000, this)
    try {
      connecting.await(20000, TimeUnit.MILLISECONDS)
      log.info("Zookeeper连接成功")
    } catch {
      case e: InterruptedException =>
        log.info("InterruptedException, Zookeeper连接失败", e)
      case e: Exception =>
        log.error("Exception ", e)
    }
  }

  def getZooKeeper: ZooKeeper = zooKeeper

  /**
   * 创建根节点
   * @param rootNodeName
   * @return
   */
  def createRootNode(rootNodeName: String): String = {
    val createMode = CreateMode.PERSISTENT
    createRootNode(rootNodeName, createMode)
  }

  def createRootNode(rootNodeName: String, createMode: CreateMode): String = {
    syncRootNodeClass.synchronized {
      try {
        val existsStat = getZooKeeper.exists(rootNodeName, false)
        if (existsStat == null)
          getZooKeeper.create(rootNodeName, new Array[Byte](0), ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode)
      } catch {
        case e: KeeperException =>
          log.info("节点已存在", e)
        case e: Exception =>
          log.error("Exception ", e)
      }
    }
    rootNodeName
  }

  override def process(event: WatchedEvent): Unit = {
    if (Event.EventType.None == event.getType) {
      if (Event.KeeperState.SyncConnected == event.getState) { // 建立连接成功
        connecting.countDown()
      }
    }
    else {
      if (Event.EventType.NodeCreated == event.getType) processNodeCreated(event)
      else if (Event.EventType.NodeDeleted == event.getType) processNodeDeleted(event)
      else if (Event.EventType.NodeDataChanged == event.getType) processNodeDataChanged(event)
      else if (Event.EventType.NodeChildrenChanged == event.getType) processNodeChildrenChanged(event)
    }
  }

  def processNodeCreated(event: WatchedEvent) {}

  def processNodeDeleted(event: WatchedEvent) {}

  def processNodeDataChanged(event: WatchedEvent) {}

  def processNodeChildrenChanged(event: WatchedEvent) {}
}
