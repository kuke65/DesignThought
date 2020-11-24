<<<<<<< HEAD:cache/src/main/scala/com/akuk/dt/cache/ElasticSearchLowLevelRestClient.scala
package com.akuk.dt.cache

import java.util.Date

import org.apache.http.HttpHost
import org.apache.http.entity.ContentType
import org.apache.http.nio.entity.NStringEntity
import org.apache.http.util.EntityUtils
import org.elasticsearch.client.{Request, RestClient}
import org.slf4j.{Logger, LoggerFactory}

/**
 * ElasticSearch v7.9.0 Low Level Rest Client
 *
 * @author Shuheng.Zhang
 * @date 2020-10-29
 */
object ElasticSearchLowLevelRestClient {

  private val log = LoggerFactory.getLogger(getClass.getName)

  /**
   * rest客户端
   */
  lazy val restClient = RestClient.builder(
    new HttpHost(Option.apply(System.getProperty("hostname")).getOrElse("127.0.0.1"), 9200, "http"),
    new HttpHost(Option.apply(System.getProperty("hostname")).getOrElse("127.0.0.1"), 9201, "http")).build()

  /**
   * 设置超时时间和线程数
   * val builder: RestClientBuilder = RestClient.builder(new HttpHost("localhost", 9200))
   * .setRequestConfigCallback(
   * (requestConfigBuilder: RequestConfig.Builder) =>
   *         requestConfigBuilder.setConnectTimeout(5000).setSocketTimeout(60000)
   * ).setHttpClientConfigCallback(
   * (httpClientBuilder: HttpAsyncClientBuilder) =>
   *         httpClientBuilder.setDefaultIOReactorConfig(IOReactorConfig.custom.setIoThreadCount(1).build))
   *
   */

  /**
   * 认证方式Authentication methods
   * 1)Basic authentication
   * 2)token service
   * val defaultHeaders: Array[Nothing] = Array[Nothing](new BasicHeader("Authorization", "Bearer u6iuAxZ0RG1Kcm5jVFI4eU4tZU9aVFEwT2F3"))
   * builder.setDefaultHeaders(defaultHeaders)
   * 3)API keys
   * new BasicHeader("Authorization", "ApiKey " + apiKeyAuth)
   */

  /**
   * Encrypted communication
   * pkcs12
   */

  def performingRequests() {
    /**
     *
     * val builder = RestClient.builder(
     * new HttpHost("localhost", 9200, "http"))
     *
     * 设置默认的请求头
     * val defaultHeaders: Array[Header] = Array[Header](new BasicHeader("header", "value"))
     * builder.setDefaultHeaders(defaultHeaders)
     *
     * 节点故障监听
     * builder.setFailureListener(new RestClient.FailureListener() {
     * def onFailure(node: Node): Unit = {}
     * })
     *
     * 节点选择器过滤客户端将向其自身设置的请求中的客户端发送请求的节点
     * 例如，在启用嗅探功能时，这可以防止阻止向专用主节点发送请求。默认情况下，客户端将请求发送到每个已配置的节点。
     * builder.setNodeSelector(NodeSelector.SKIP_DEDICATED_MASTERS)
     *
     * 设置同步请求回调、超时、身份验证等
     * builder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
     * override def customizeRequestConfig(requestConfigBuilder: RequestConfig.Builder): RequestConfig.Builder = requestConfigBuilder.setSocketTimeout(10000)
     * })
     *
     * 设置异步回调,ssl加密的通信等
     * builder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
     * override def customizeHttpClient(httpClientBuilder: HttpAsyncClientBuilder): HttpAsyncClientBuilder = httpClientBuilder.setProxy(new HttpHost("proxy", 9000, "http"))
     * })
     *
     */

    val request = new Request("GET", "/posts/_doc/1")

    /**
     * 异步请求
     * val request: Request = new Request("GET", "/")
     * val cancellable = restClient.performRequestAsync(request, new ResponseListener() {
     * override def onSuccess(response: Response): Unit = {
     * }
     *
     * override def onFailure(exception: Exception): Unit = {
     * }
     * })
     */

    request.addParameter("pretty", "true") // 设置请求参数

    request.setEntity(new NStringEntity("{\"json\":\"text\"}", ContentType.APPLICATION_JSON)) // 设置body

    /**
     * 也可以这样设置body, ContentType = application/json
     * request.setJsonEntity("{\"json\":\"text\"}")
     */

    /**
     * 请求选项 RequestOptions, 自定义异步响应缓冲,最多缓冲100MB的响应
     * val builder = RequestOptions.DEFAULT.toBuilder()
     *     builder.addHeader("Authorization", "Bearer " + TOKEN)
     *     builder.setHttpAsyncResponseConsumerFactory(
     * new HttpAsyncResponseConsumerFactory
     * .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024))
     * val COMMON_OPTIONS = builder.build()
     *
     * 拓展请求头
     * val options: RequestOptions.Builder = COMMON_OPTIONS.toBuilder
     * options.addHeader("cats", "knock things off of other things")
     * request.setOptions(options)
     */

    val response = restClient.performRequest(request)
    val host = response.getHost // 响应的主机
    val statusCode = response.getStatusLine.getStatusCode // 响应状态
    val headers = response.getHeaders // 响应请求头
    val responseBody = EntityUtils.toString(response.getEntity) // 响应body
    println(responseBody)
  }

  def indexDataPost(index: String, esType: String, docId: String, jsonString: String) = {

    var endpoint = s"/${index}/${esType}"
    var method = "POST"
    if(Option.apply(docId).isDefined && docId.trim.length > 0) {
      endpoint = s"/${index}/${esType}/${docId}"
      method = "PUT"
    }

    log.info(s"""\n ## ElasticSearchLowLevelRestClient.indexDataPost.requestParam
         | ${endpoint}?json=${jsonString}""".stripMargin)

    val request = new Request(method, endpoint)
    request.addParameter("pretty", "true") // 设置请求参数
    request.setJsonEntity(jsonString)
    val response = restClient.performRequest(request)
    val host = response.getHost // 响应的主机
    val statusCode = response.getStatusLine.getStatusCode // 响应状态
    val headers = response.getHeaders // 响应请求头
    val responseBody = EntityUtils.toString(response.getEntity) // 响应body

    log.info(
      s"""\n ## ElasticSearchLowLevelRestClient.indexDataPost.responseResult
         | host=${host},statusCode=${statusCode},headers=${headers.map(h=>s"${h.getName}:${h.getValue}").mkString(",")},responseBody=${responseBody}""".stripMargin)

    responseBody

  }

  def indexDataGet(index: String, esType: String, docId: String, jsonString: String) = {

    log.info(s"""\n ## ElasticSearchLowLevelRestClient.indexDataGet.requestParam
                | ${index}/${esType}/${docId}?json=${jsonString}""".stripMargin)

    val request = new Request("GET", s"/${index}/${esType}/${docId}")
    request.addParameter("pretty", "true") // 设置请求参数
    // request.setJsonEntity(jsonString)
    val response = restClient.performRequest(request)
    val host = response.getHost // 响应的主机
    val statusCode = response.getStatusLine.getStatusCode // 响应状态
    val headers = response.getHeaders // 响应请求头
    val responseBody = EntityUtils.toString(response.getEntity) // 响应body

    log.info(
      s"""\n ## ElasticSearchLowLevelRestClient.indexDataGet.responseResult
         | host=${host},statusCode=${statusCode},headers=${headers.map(h=>s"${h.getName}:${h.getValue}").mkString(",")},responseBody=${responseBody}""".stripMargin)

    responseBody

  }

  /**
   * @param index
   * @param esType
   * @param docId
   * @param jsonString
   * @return
   */
  def indexDataPut(index: String, esType: String, docId: String, jsonString: String) = {

    /**
     * post请求新增, put请求有更新没有新增
     */

    var endpoint = s"/${index}/${esType}"
    var method = "POST"
    if(Option.apply(docId).isDefined && docId.trim.length > 0) {
      endpoint = s"/${index}/${esType}/${docId}"
      method = "PUT"
    }

    log.info(s"""\n ## ElasticSearchLowLevelRestClient.indexDataPut.requestParam
                | method=${method}, ${endpoint}?json=${jsonString}""".stripMargin)

    val request = new Request(method, endpoint)
    request.setJsonEntity(jsonString)
    val response = restClient.performRequest(request)
    val host = response.getHost // 响应的主机
    val statusCode = response.getStatusLine.getStatusCode // 响应状态
    val headers = response.getHeaders // 响应请求头
    val responseBody = EntityUtils.toString(response.getEntity) // 响应body

    log.info(
      s"""\n ## ElasticSearchLowLevelRestClient.indexDataPut.responseResult
         | host=${host},statusCode=${statusCode},headers=${headers.map(h=>s"${h.getName}:${h.getValue}").mkString(",")},responseBody=${responseBody}""".stripMargin)

    responseBody

  }

  def indexDataDelete(index: String, esType: String, docId: String, jsonString: String) = {
    var endpoint = s"/${index}/${esType}"
    if(Option.apply(docId).isDefined && docId.trim.length > 0) {
      endpoint = s"/${index}/${esType}/${docId}"
    }else if(Option.apply(esType).isEmpty || esType.trim.length == 0) {
      endpoint = s"/${index}"
    }

    log.info(s"""\n ## ElasticSearchLowLevelRestClient.indexDataDelete.requestParam
                | ${endpoint}?json=${jsonString}""".stripMargin)

    val request = new Request("DELETE", endpoint)
    request.setJsonEntity(jsonString)
    val response = restClient.performRequest(request)
    val host = response.getHost // 响应的主机
    val statusCode = response.getStatusLine.getStatusCode // 响应状态
    val headers = response.getHeaders // 响应请求头
    val responseBody = EntityUtils.toString(response.getEntity) // 响应body

    log.info(
      s"""\n ## ElasticSearchLowLevelRestClient.indexDataDelete.responseResult
         | host=${host},statusCode=${statusCode},headers=${headers.map(h=>s"${h.getName}:${h.getValue}").mkString(",")},responseBody=${responseBody}""".stripMargin)

    responseBody

  }

}
=======
package com.akuk.dt.cache

import java.util.Date

import org.apache.http.HttpHost
import org.apache.http.entity.ContentType
import org.apache.http.nio.entity.NStringEntity
import org.apache.http.util.EntityUtils
import org.elasticsearch.client.{Request, RestClient}
import org.slf4j.{Logger, LoggerFactory}

/**
 * ElasticSearch v7.9.0 Low Level Rest Client
 *
 * @author Shuheng.Zhang
 * @date 2020-10-29
 */
object ElasticSearchLowLevelRestClient {

  private val log = LoggerFactory.getLogger(getClass.getName)

  /**
   * rest客户端
   */
  lazy val restClient = RestClient.builder(
    new HttpHost(Option.apply(System.getProperty("hostname")).getOrElse("127.0.0.1"), 9200, "http"),
    new HttpHost(Option.apply(System.getProperty("hostname")).getOrElse("127.0.0.1"), 9201, "http")).build()

  /**
   * 设置超时时间和线程数
   * val builder: RestClientBuilder = RestClient.builder(new HttpHost("localhost", 9200))
   * .setRequestConfigCallback(
   * (requestConfigBuilder: RequestConfig.Builder) =>
   *         requestConfigBuilder.setConnectTimeout(5000).setSocketTimeout(60000)
   * ).setHttpClientConfigCallback(
   * (httpClientBuilder: HttpAsyncClientBuilder) =>
   *         httpClientBuilder.setDefaultIOReactorConfig(IOReactorConfig.custom.setIoThreadCount(1).build))
   *
   */

  /**
   * 认证方式Authentication methods
   * 1)Basic authentication
   * 2)token service
   * val defaultHeaders: Array[Nothing] = Array[Nothing](new BasicHeader("Authorization", "Bearer u6iuAxZ0RG1Kcm5jVFI4eU4tZU9aVFEwT2F3"))
   * builder.setDefaultHeaders(defaultHeaders)
   * 3)API keys
   * new BasicHeader("Authorization", "ApiKey " + apiKeyAuth)
   */

  /**
   * Encrypted communication
   * pkcs12
   */

  def performingRequests() {
    /**
     *
     * val builder = RestClient.builder(
     * new HttpHost("localhost", 9200, "http"))
     *
     * 设置默认的请求头
     * val defaultHeaders: Array[Header] = Array[Header](new BasicHeader("header", "value"))
     * builder.setDefaultHeaders(defaultHeaders)
     *
     * 节点故障监听
     * builder.setFailureListener(new RestClient.FailureListener() {
     * def onFailure(node: Node): Unit = {}
     * })
     *
     * 节点选择器过滤客户端将向其自身设置的请求中的客户端发送请求的节点
     * 例如，在启用嗅探功能时，这可以防止阻止向专用主节点发送请求。默认情况下，客户端将请求发送到每个已配置的节点。
     * builder.setNodeSelector(NodeSelector.SKIP_DEDICATED_MASTERS)
     *
     * 设置同步请求回调、超时、身份验证等
     * builder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
     * override def customizeRequestConfig(requestConfigBuilder: RequestConfig.Builder): RequestConfig.Builder = requestConfigBuilder.setSocketTimeout(10000)
     * })
     *
     * 设置异步回调,ssl加密的通信等
     * builder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
     * override def customizeHttpClient(httpClientBuilder: HttpAsyncClientBuilder): HttpAsyncClientBuilder = httpClientBuilder.setProxy(new HttpHost("proxy", 9000, "http"))
     * })
     *
     */

    val request = new Request("GET", "/posts/_doc/1")

    /**
     * 异步请求
     * val request: Request = new Request("GET", "/")
     * val cancellable = restClient.performRequestAsync(request, new ResponseListener() {
     * override def onSuccess(response: Response): Unit = {
     * }
     *
     * override def onFailure(exception: Exception): Unit = {
     * }
     * })
     */

    request.addParameter("pretty", "true") // 设置请求参数

    request.setEntity(new NStringEntity("{\"json\":\"text\"}", ContentType.APPLICATION_JSON)) // 设置body

    /**
     * 也可以这样设置body, ContentType = application/json
     * request.setJsonEntity("{\"json\":\"text\"}")
     */

    /**
     * 请求选项 RequestOptions, 自定义异步响应缓冲,最多缓冲100MB的响应
     * val builder = RequestOptions.DEFAULT.toBuilder()
     *     builder.addHeader("Authorization", "Bearer " + TOKEN)
     *     builder.setHttpAsyncResponseConsumerFactory(
     * new HttpAsyncResponseConsumerFactory
     * .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024))
     * val COMMON_OPTIONS = builder.build()
     *
     * 拓展请求头
     * val options: RequestOptions.Builder = COMMON_OPTIONS.toBuilder
     * options.addHeader("cats", "knock things off of other things")
     * request.setOptions(options)
     */

    val response = restClient.performRequest(request)
    val host = response.getHost // 响应的主机
    val statusCode = response.getStatusLine.getStatusCode // 响应状态
    val headers = response.getHeaders // 响应请求头
    val responseBody = EntityUtils.toString(response.getEntity) // 响应body
    println(responseBody)
  }

  def indexDataPost(index: String, esType: String, docId: String, jsonString: String) = {

    var endpoint = s"/${index}/${esType}"
    var method = "POST"
    if(Option.apply(docId).isDefined && docId.trim.length > 0) {
      endpoint = s"/${index}/${esType}/${docId}"
      method = "PUT"
    }

    log.info(s"""\n ## ElasticSearchLowLevelRestClient.indexDataPost.requestParam
         | ${endpoint}?json=${jsonString}""".stripMargin)

    val request = new Request(method, endpoint)
    request.addParameter("pretty", "true") // 设置请求参数
    request.setJsonEntity(jsonString)
    val response = restClient.performRequest(request)
    val host = response.getHost // 响应的主机
    val statusCode = response.getStatusLine.getStatusCode // 响应状态
    val headers = response.getHeaders // 响应请求头
    val responseBody = EntityUtils.toString(response.getEntity) // 响应body

    log.info(
      s"""\n ## ElasticSearchLowLevelRestClient.indexDataPost.responseResult
         | host=${host},statusCode=${statusCode},headers=${headers.map(h=>s"${h.getName}:${h.getValue}").mkString(",")},responseBody=${responseBody}""".stripMargin)

    responseBody

  }

  def indexDataGet(index: String, esType: String, docId: String, jsonString: String) = {

    log.info(s"""\n ## ElasticSearchLowLevelRestClient.indexDataGet.requestParam
                | ${index}/${esType}/${docId}?json=${jsonString}""".stripMargin)

    val request = new Request("GET", s"/${index}/${esType}/${docId}")
    request.addParameter("pretty", "true") // 设置请求参数
    // request.setJsonEntity(jsonString)
    val response = restClient.performRequest(request)
    val host = response.getHost // 响应的主机
    val statusCode = response.getStatusLine.getStatusCode // 响应状态
    val headers = response.getHeaders // 响应请求头
    val responseBody = EntityUtils.toString(response.getEntity) // 响应body

    log.info(
      s"""\n ## ElasticSearchLowLevelRestClient.indexDataGet.responseResult
         | host=${host},statusCode=${statusCode},headers=${headers.map(h=>s"${h.getName}:${h.getValue}").mkString(",")},responseBody=${responseBody}""".stripMargin)

    responseBody

  }

  /**
   * @param index
   * @param esType
   * @param docId
   * @param jsonString
   * @return
   */
  def indexDataPut(index: String, esType: String, docId: String, jsonString: String) = {

    /**
     * post请求新增, put请求有更新没有新增
     */

    var endpoint = s"/${index}/${esType}"
    var method = "POST"
    if(Option.apply(docId).isDefined && docId.trim.length > 0) {
      endpoint = s"/${index}/${esType}/${docId}"
      method = "PUT"
    }

    log.info(s"""\n ## ElasticSearchLowLevelRestClient.indexDataPut.requestParam
                | method=${method}, ${endpoint}?json=${jsonString}""".stripMargin)

    val request = new Request(method, endpoint)
    request.setJsonEntity(jsonString)
    val response = restClient.performRequest(request)
    val host = response.getHost // 响应的主机
    val statusCode = response.getStatusLine.getStatusCode // 响应状态
    val headers = response.getHeaders // 响应请求头
    val responseBody = EntityUtils.toString(response.getEntity) // 响应body

    log.info(
      s"""\n ## ElasticSearchLowLevelRestClient.indexDataPut.responseResult
         | host=${host},statusCode=${statusCode},headers=${headers.map(h=>s"${h.getName}:${h.getValue}").mkString(",")},responseBody=${responseBody}""".stripMargin)

    responseBody

  }

  def indexDataDelete(index: String, esType: String, docId: String, jsonString: String) = {
    var endpoint = s"/${index}/${esType}"
    if(Option.apply(docId).isDefined && docId.trim.length > 0) {
      endpoint = s"/${index}/${esType}/${docId}"
    }else if(Option.apply(esType).isEmpty || esType.trim.length == 0) {
      endpoint = s"/${index}"
    }

    log.info(s"""\n ## ElasticSearchLowLevelRestClient.indexDataDelete.requestParam
                | ${endpoint}?json=${jsonString}""".stripMargin)

    val request = new Request("DELETE", endpoint)
    request.setJsonEntity(jsonString)
    val response = restClient.performRequest(request)
    val host = response.getHost // 响应的主机
    val statusCode = response.getStatusLine.getStatusCode // 响应状态
    val headers = response.getHeaders // 响应请求头
    val responseBody = EntityUtils.toString(response.getEntity) // 响应body

    log.info(
      s"""\n ## ElasticSearchLowLevelRestClient.indexDataDelete.responseResult
         | host=${host},statusCode=${statusCode},headers=${headers.map(h=>s"${h.getName}:${h.getValue}").mkString(",")},responseBody=${responseBody}""".stripMargin)

    responseBody

  }

}
>>>>>>> refs/remotes/origin/main:src/main/scala/com/akuk/dt/cache/ElasticSearchLowLevelRestClient.scala
