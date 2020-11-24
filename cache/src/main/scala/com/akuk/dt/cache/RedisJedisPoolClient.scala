package com.akuk.dt.cache

import java.util

import com.akuk.dt.cache.RedisRoutes.ClientCredentials
import org.slf4j.LoggerFactory
import redis.clients.jedis.JedisSentinelPool

object RedisJedisPoolClient {

  private val log = LoggerFactory.getLogger(getClass.getName)

  private val CRUD_PREFIX_NAME = "dt:crud:"
  private val CLIENT_ACTIVE_STATUS = "1"
  private val CLIENT_INACTIVE_STATUS = "0"

  /**
    * 简单的返回格式定义
    */
  private val RESULT_OK = "ok"
  private val RESULT_FAIL = "fail"

  /**
    * 默认懒加载
    */
  private var pool: JedisSentinelPool = _
  private val master: String = "mymaster"
  private val sentinelsAsString: String = "192.168.1.3:26379"
  private val password: String = null
  private val sentinelsList = sentinelsAsString.split(",")
  private val sentinels = new util.HashSet[String](util.Arrays.asList(sentinelsList: _*))

  /**
    * 初始化redis连接池
    */
  if (Option.apply(password).isEmpty && Option.apply(pool).isEmpty) {
    pool = new JedisSentinelPool(master, sentinels)
  } else if (Option.apply(pool).isEmpty) {
    pool = new JedisSentinelPool(master, sentinels, password)
  }


  /**
    * 保存客户端认证信息
    *
    * @param clientCreds
    * @return
    */
  def storeClientCredentials(clientCreds: ClientCredentials): String = {
    val credentials = new util.HashMap[String, String]
    credentials.put("_id", clientCreds.id)
    credentials.put("secret", clientCreds.secret)
    credentials.put("name", clientCreds.name)
    credentials.put("uri", clientCreds.uri)
    credentials.put("descr", clientCreds.descr)
    credentials.put("type", String.valueOf(clientCreds.ctype))
    credentials.put("status", String.valueOf(clientCreds.status))
    credentials.put("created", String.valueOf(clientCreds.created))
    credentials.put("scope", String.valueOf(clientCreds.scope))
    credentials.put("details", "")
    val jedis = pool.getResource
    jedis.hmset(CRUD_PREFIX_NAME + clientCreds.id, credentials)
    jedis.close()
    RESULT_OK
  }


  /**
    * 更新客户端认证信息
    *
    * @param clientCreds
    * @return
    */
  def updateClientCredentials(clientCreds: ClientCredentials): String = {
    val credentials = new util.HashMap[String, String]
    credentials.put("_id", clientCreds.id)
    credentials.put("secret", clientCreds.secret)
    credentials.put("name", clientCreds.name)
    credentials.put("uri", clientCreds.uri)
    credentials.put("descr", clientCreds.descr)
    credentials.put("type", String.valueOf(clientCreds.ctype))
    credentials.put("status", String.valueOf(clientCreds.status))
    credentials.put("created", String.valueOf(clientCreds.created))
    credentials.put("scope", String.valueOf(clientCreds.scope))
    credentials.put("details", "")
    val jedis = pool.getResource
    jedis.hmset(CRUD_PREFIX_NAME + clientCreds.id, credentials)
    jedis.close()
    RESULT_OK
  }


  /**
    * 验证客户端认证信息
    *
    * @param clientId
    * @param clientSecret
    * @return
    */
  def validClient(clientId: String, clientSecret: String): String = {
    val jedis = pool.getResource
    val key = CRUD_PREFIX_NAME + clientId
    val secret = jedis.hget(key, "secret")
    val status = jedis.hget(key, "status")
    jedis.close()
    // log.info(s"RedisCache.validClient($clientId, $clientSecret) secret: $secret , status = $status ")
    if (clientSecret.equals(secret) && CLIENT_ACTIVE_STATUS.equals(status)) {
      RESULT_OK
    } else {
      RESULT_FAIL
    }
  }

  /**
    * 移除客户端认证信息
    *
    * @param clientId
    * @return
    */
  def removeClientId(clientId: String): String = {
    val jedis = pool.getResource
    jedis.expire(CRUD_PREFIX_NAME + clientId, 0)
    jedis.close()
    RESULT_OK
  }


}
