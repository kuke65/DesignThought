package com.akuk.dt.cache.zk

trait ZookeeperLock {

  /**
   * 加锁
   * @param nodeName
   * @param itemName
   * @return
   */
  def lock(nodeName: String, itemName: String): Boolean

  /**
   * 释放锁
   * @param nodeName
   * @param itemName
   * @return
   */
  def release(nodeName: String, itemName: String): Boolean

  /**
   * 锁是否存在
   * @param nodeName
   * @return
   */
  def exists(nodeName: String): Boolean


}
