package com.akuk.dt.cache.zk

import org.apache.zookeeper.{CreateMode, KeeperException, ZooDefs}

class ClientActorLock(address: String) extends ZooKeeperBase(address) with ZookeeperLock {

  /**
   * 加锁
   *
   * @param nodeName
   * @param itemName
   * @return
   */
  override def lock(nodeName: String, itemName: String): Boolean = {
    var result = false

    // 确保根节点存在，并且创建为容器节点
    super.createRootNode("/" + nodeName, CreateMode.PERSISTENT)

    try {
      val fullNodeName = "/" + nodeName + "/" + itemName
      val existsStat = getZooKeeper.exists(fullNodeName, false)
      if (existsStat == null) {
        getZooKeeper.create(fullNodeName, new Array[Byte](0), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL)
        result = true
      }
    } catch {
      case e: KeeperException =>
        log.info("KeeperException", e)
      case e: Exception =>
        log.error("Exception ", e)
    }
    result
  }

  /**
   * 释放锁
   *
   * @param nodeName
   * @param itemName
   * @return
   */
  override def release(nodeName: String, itemName: String): Boolean = ???

  /**
   * 锁是否存在
   *
   * @param nodeName
   * @return
   */
  override def exists(nodeName: String): Boolean = ???
}
