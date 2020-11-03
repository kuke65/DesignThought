package com.akuk.dt.cache

import java.util.Date

import org.apache.http.HttpHost
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.client.{RequestOptions, RestClient, RestHighLevelClient}
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.builder.SearchSourceBuilder


/**
 * ElasticSearch v7.9.0 High Level Rest Client
 *
 * @author Shuheng.Zhang
 * @date 2020-10-29
 */
object ElasticSearchHighLevelRestClient {
  // 创建Rest客户端
  val client = new RestHighLevelClient(RestClient.builder(new HttpHost(Option.apply(System.getProperty("hostname")).getOrElse("127.0.0.1"), 9200 ,"http") ))
  val defaultOption = RequestOptions.DEFAULT

  /**
   * DocumentAPIs
   */
  case object DocumentAPIs {
    // 创建索引
    def indexAPI(): Unit = {

      val request = new IndexRequest("posts").id("1").source(
        "user", "kimchy",
        "postDate", System.currentTimeMillis(),
        "message", "trying out Elasticsearch")

      /**
       * XContentBuilder 对象
       * val builder = XContentFactory.jsonBuilder
       * builder.startObject
       * builder.field("user", "kimchy")
       * builder.timeField("postDate", new Nothing)
       * builder.field("message", "trying out Elasticsearch")
       * builder.endObject
       * val indexRequest: IndexRequest = new IndexRequest("posts").id("1").source(builder)
       *
       * 可选参数
       * request.routing("routing") // 路由
       * request.timeout(TimeValue.timeValueSeconds(1));
       * request.timeout("1s") // 超时时间设置
       * request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL)
       * request.setRefreshPolicy("wait_for") // 刷新策略 Refresh policy
       * request.version(2) // version
       * request.versionType(VersionType.EXTERNAL) //versionType
       * request.opType(DocWriteRequest.OpType.CREATE)
       * request.opType("create") // create和index,默认index
       * request.setPipeline("pipeline")
       *
       */

      /**
       * 异步执行
       * val listener = new ActionListener[IndexResponse] {
       * override def onResponse(indexResponse: IndexResponse) { do something}
       * override def onFailure(e: Exception) { do something }
       * }
       * client.indexAsync(request, defaultOption, listener)
       */

      val response = client.index(request, defaultOption)

      /**
       * 检索返回的信息
       * val index: String = indexResponse.getIndex
       * val id: String = indexResponse.getId
       * if (indexResponse.getResult eq DocWriteResponse.Result.CREATED)  {
       * // 首次创建
       * }
       * else if (indexResponse.getResult eq DocWriteResponse.Result.UPDATED)  {
       * // 已存在doc 被重写的情况
       * }
       * val shardInfo: ReplicationResponse.ShardInfo = indexResponse.getShardInfo
       * if (shardInfo.getTotal != shardInfo.getSuccessful)  {
       * // 处理分片不一致
       * }
       * if (shardInfo.getFailed > 0)  {
       * for (failure <- shardInfo.getFailures) {
       * val reason: String = failure.reason
       * // 其他的问题
       * }
       * }
       *
       * e: ElasticsearchException 异常
       * version conflict  // 版本冲突
       * DocWriteRequest.OpType.CREATE // 已存在doc
       * e.status() == RestStatus.CONFLICT
       */

      print(response.getResult)
    }

    def getApi() = ???

    def getSourceApi() = ???

    def existsApi() = ???

    def deleteApi() = ???

    def updateApi() = ???

    def termVectorsApi() = ???

    def bulkApi() = ???

    def mutilGetApi() = ???

    def ReindexApi() = ???

    def updateByQueryApi() = ???

    def deleteByQueryApi() = ???

    def rethrottleApi() = ???

    def MultiTermVectorsApi() = ???

  }

  /**
   *  SearchAPIs
   */
  object SearchAPIs {
    /**
     * 同SQL 查询代码
     * SELECT SCORE(), * FROM "social-*"
     * WHERE match(message, 'myProduct')
     * ORDER BY SCORE() DESC
     *
     * SELECT state, COUNT(*) AS state_count FROM "social-*"
     * WHERE match(message, 'myProduct')
     * GROUP BY state
     * ORDER BY COUNT(*)
     * LIMIT 10
     */
    def searchApi(): Unit = {

      // val searchRequest　＝　new SearchRequest("social-*") 同下
      val searchRequest = new SearchRequest
      searchRequest.indices("social-*") // 需要匹配索引

      /**
       * 可选参数
       * searchRequest.routing("routing") // 路由(根据业务逻辑使用)
       *
       * IndicesOptions:
       * 1)STRICT_EXPAND_OPEN = ALLOW_NO_INDICES, WildcardStates.OPEN
       *   允许索引不存在|通配符作用域OPEN
       * 2)STRICT_EXPAND_OPEN_HIDDEN = ALLOW_NO_INDICES, WildcardStates.OPEN, WildcardStates.HIDDEN
       *   允许索引不存在|通配符作用域OPEN,HIDDEN
       * 3)LENIENT_EXPAND_OPEN = ALLOW_NO_INDICES, IGNORE_UNAVAILABLE, WildcardStates.OPEN
       *   允许索引不存在,忽略不可用索引|通配符作用域OPEN
       * 4)LENIENT_EXPAND_OPEN_HIDDEN = ALLOW_NO_INDICES, IGNORE_UNAVAILABLE, WildcardStates.OPEN, WildcardStates.HIDDEN
       *   允许索引不存在,忽略不可用索引|通配符作用域OPEN,HIDDEN
       * 5)LENIENT_EXPAND_OPEN_CLOSED = ALLOW_NO_INDICES, IGNORE_UNAVAILABLE, WildcardStates.OPEN, WildcardStates.CLOSED
       *   允许索引不存在,忽略不可用索引|通配符作用域OPEN,CLOSE
       * 6)LENIENT_EXPAND_OPEN_CLOSED_HIDDEN = ALLOW_NO_INDICES, IGNORE_UNAVAILABLE, WildcardStates.OPEN, WildcardStates.CLOSED, WildcardStates.HIDDEN
       *   允许索引不存在,忽略不可用索引|通配符作用域OPEN,CLOSE,HIDDEN
       * 7)STRICT_EXPAND_OPEN_CLOSED = ALLOW_NO_INDICES, WildcardStates.OPEN, WildcardStates.CLOSED
       *   允许索引不存在|通配符作用域OPEN,CLOSE
       * 8)STRICT_EXPAND_OPEN_CLOSED_HIDDEN = ALLOW_NO_INDICES, WildcardStates.OPEN, WildcardStates.CLOSED, WildcardStates.HIDDEN
       *   允许索引不存在|通配符作用域OPEN,CLOSE,HIDDEN
       * 9)STRICT_EXPAND_OPEN_FORBID_CLOSED = ALLOW_NO_INDICES, FORBID_CLOSED_INDICES, WildcardStates.OPEN
       *   允许索引不存在,禁止关闭的索引|通配符作用域OPEN
       * 10)STRICT_EXPAND_OPEN_HIDDEN_FORBID_CLOSED = ALLOW_NO_INDICES, FORBID_CLOSED_INDICES, WildcardStates.OPEN, WildcardStates.HIDDEN
       *   允许索引不存在,禁止关闭的索引|通配符作用域OPEN,HIDDEN
       * 11)STRICT_EXPAND_OPEN_FORBID_CLOSED_IGNORE_THROTTLED = ALLOW_NO_INDICES, FORBID_CLOSED_INDICES, IGNORE_THROTTLED, WildcardStates.OPEN
       *   允许索引不存在,禁止关闭的索引,IGNORE_THROTTLED(Version)|通配符作用域OPEN
       * 12)STRICT_SINGLE_INDEX_NO_EXPAND_FORBID_CLOSED = FORBID_ALIASES_TO_MULTIPLE_INDICES, FORBID_CLOSED_INDICES
       *   禁止操作多个索引或别名,禁止关闭的索引|空枚举值
       *
       * searchRequest.indicesOptions(IndicesOptions.lenientExpandOpen)
       *
       * preference:
       * _local 优先本地节点
       * _prefer_nodes 首选节点查询
       * _shards 路由到明确的分片
       * _only_nodes 仅节点
       * _only_local 仅本地
       *
       * searchRequest.preference("_local") // 优先在本地节点有的分片中查询
       */

      val searchSourceBuilder = new SearchSourceBuilder

      /**
       * 构建查询
       * 1)叶子查询子句 match(分析搜索词)，term(确切术语,不分析搜索词)或 range(范围keyword或者id)查询条件
       * 2)复合查询子句 bool(必须非必须),dis_max(子查询得分 * tie_breaker)或constant_score(term匹配固定得分)
       * 3)允许昂贵的查询
       * val matchQueryBuilder: MatchQueryBuilder = new MatchQueryBuilder("user", "kimchy") // 匹配field "user" = "kimchy"
       * matchQueryBuilder.fuzziness(Fuzziness.AUTO) // fuzziness,匹配允许的最大编辑距离
       * matchQueryBuilder.prefixLength(3) // prefixLength,前3位不模糊
       * matchQueryBuilder.maxExpansions(10) // maxExpansions,指定routing模糊后10位,不指定取各分片返回结果
       * 使用 match构建对象查询
       * searchSourceBuilder.query(matchQueryBuilder)
       */

      searchSourceBuilder.query(QueryBuilders.matchAllQuery)
      searchSourceBuilder.aggregation(AggregationBuilders.terms("top_10_states").field("state").size(10))

      /**
       * 使用AggregationBuilders,聚合查询
       * val aggregation: TermsAggregationBuilder = AggregationBuilders.terms("by_company").field("company.keyword")
       * aggregation.subAggregation(AggregationBuilders.avg("average_age").field("age"))
       * searchSourceBuilder.aggregation(aggregation)
       *
       * 分析查询 和聚合
       * "profile": {"shards": [{"id":"[2aE02wS1R8q_QFnYu6vDVQ][my-index-000001][0]","searchs":[...],"aggregations":[...] }]
       * searchSourceBuilder.profile(true)
       */

      /**
       * 使用SearchSourceBuilder
       * SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
       * sourceBuilder.query(QueryBuilders.termQuery("user", "kimchy")) //
       * sourceBuilder.from(0)  // 开始查询的位置,默认0
       * sourceBuilder.size(5)  // 查询个数,默认10
       * sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS)) // 设置查询超时间
       *
       * 创建SortBuilder实现排序
       * sourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC))  // score倒序
       * sourceBuilder.sort(new FieldSortBuilder("id").order(SortOrder.ASC))  // id 升序
       *
       * 过滤
       * sourceBuilder.fetchSource(false) // 不过滤
       * val includeFields: Array[String] = Array[String]("title", "innerObject.*")
       * val excludeFields: Array[String] = Array[String]("user")
       * sourceBuilder.fetchSource(includeFields, excludeFields) // 过滤指定的Fields
       *
       * 建议
       * val termSuggestionBuilder = SuggestBuilders.termSuggestion("user").text("kmichy")
       * val suggestBuilder: SuggestBuilder = new SuggestBuilder
       * suggestBuilder.addSuggestion("suggest_user", termSuggestionBuilder)
       * searchSourceBuilder.suggest(suggestBuilder)
       *
       * 使用HighlightBuilder
       * val searchSourceBuilder: SearchSourceBuilder = new SearchSourceBuilder
       * val highlightBuilder: HighlightBuilder = new HighlightBuilder
       * val highlightTitle: HighlightBuilder.Field = new HighlightBuilder.Field("title")
       * highlightTitle.highlighterType("unified")
       * highlightBuilder.field(highlightTitle)
       * val highlightUser: HighlightBuilder.Field = new HighlightBuilder.Field("user")
       * highlightBuilder.field(highlightUser)
       * searchSourceBuilder.highlighter(highlightBuilder)
       * #preTags(String ... )高亮文本
       */

      searchRequest.source(searchSourceBuilder)

      /**
       * 异步查询
       * val listener = new ActionListener[SearchResponse] {
       *  override def onResponse(searchResponse: SearchResponse) { do Something}
       *  override def onFailure(e: Exception) {do Something}
       * }
       * client.searchAsync(searchRequest, defaultOption, listener)
       *
       */

      val searchResponse = client.search(searchRequest, defaultOption)

      /**
       * 查询相应
       * 1) 相应信息
       * val status: RestStatus = searchResponse.status
       * val took: TimeValue = searchResponse.getTook
       * val terminatedEarly: Boolean = searchResponse.isTerminatedEarly
       * val timedOut: Boolean = searchResponse.isTimedOut
       *
       * 2) 分片信息
       * val totalShards: Int = searchResponse.getTotalShards
       * val successfulShards: Int = searchResponse.getSuccessfulShards
       * val failedShards: Int = searchResponse.getFailedShards
       * for (failure <- searchResponse.getShardFailures)  {
       * // failures should be handled here
       * }
       *
       * 3) 历史信息
       * val hits: SearchHits = searchResponse.getHits
       * val totalHits: TotalHits = hits.getTotalHits
       * val numHits: Long = totalHits.value
       * val relation: TotalHits.Relation = totalHits.relation
       * val maxScore: Float = hits.getMaxScore
       * val searchHits: Array[SearchHit] = hits.getHits
       * for (hit <- searchHits)  {
       * // do something with the SearchHit
       * }
       *
       * 4)score
       * val index: String = hit.getIndex
       * val id: String = hit.getId
       * val score: Float = hit.getScore
       *
       * 5)cast scenes
       * val sourceAsString: String = hit.getSourceAsString
       * val sourceAsMap: Nothing = hit.getSourceAsMap
       * val documentTitle: String = sourceAsMap.get("title").asInstanceOf[String]
       * val users: util.List[AnyRef] = sourceAsMap.get("user").asInstanceOf[util.List[AnyRef]]
       * val innerObject: Nothing = sourceAsMap.get("innerObject").asInstanceOf[Nothing]
       *
       * 解析Highlighting
       * val hits: SearchHits = searchResponse.getHits
       * for (hit <- hits.getHits)  { val highlightFields: Nothing = hit.getHighlightFields
       *   val highlight: HighlightField = highlightFields.get("title")
       *   val fragments: Array[Nothing] = highlight.fragments
       *   val fragmentString: String = fragments(0).string
       * }
       *
       * 解析聚合Aggregations
       * val aggregations: Aggregations = searchResponse.getAggregations
       * val byCompanyAggregation = aggregations.get("by_company")
       * val elasticBucket = byCompanyAggregation.getBucketByKey("Elastic")
       * val averageAge: Avg = elasticBucket.getAggregations.get("average_age")
       * val avg: Double = averageAge.getValue
       *
       * 1)  the cast to the proper aggregation interface needs to happen explicitly
       * val aggregationMap = aggregations.getAsMap
       * val companyAggregation: Nothing = aggregationMap.get("by_company").asInstanceOf[Nothing]
       *
       * 2) list
       * val aggregationList: util.List[Nothing] = aggregations.asList
       *
       * 3) iterate
       * for (agg <- aggregations)  {
       *   val `type`: String = agg.getType
       *   if (`type` == TermsAggregationBuilder.NAME)  { val elasticBucket: Nothing = (agg.asInstanceOf[Nothing]).getBucketByKey("Elastic")
       *     val numberOfDocs: Long = elasticBucket.getDocCount
       *   }
       * }
       *
       * 建议Suggestions
       * val suggest = searchResponse.getSuggest
       * val termSuggestion = suggest.getSuggestion("suggest_user")
       * for (entry <- termSuggestion.getEntries) {
       *   for (option <- entry) {
       *     val suggestText = option.getText.string
       *   }
       * }
       *
       * 分析查询 Profiling Results
       * val profilingResults: util.Map[String, ProfileShardResult] = searchResponse.getProfileResults
       * for (profilingResult <- profilingResults.entrySet)  { val key: String = profilingResult.getKey
       *   val profileShardResult: ProfileShardResult = profilingResult.getValue
       * }
       *
       * 1) QueryProfileShardResult
       * val queryProfileShardResults = profileShardResult.getQueryProfileResults
       * for (queryProfileResult <- queryProfileShardResults) {}
       *
       * 2) 检索名称,毫秒数,子查询的概要文件结果 ProfileResult
       * for (profileResult <- queryProfileResult.getQueryResults)  { val queryName: String = profileResult.getQueryName
       *   val queryTimeInMillis: Long = profileResult.getTime
       *   val profiledChildren: util.List[ProfileResult] = profileResult.getProfiledChildren
       * }
       *
       * 3) 收集分析查询 CollectorResult
       * CollectorResult collectorResult = queryProfileResult.getCollectorResult();
       * String collectorName = collectorResult.getName();
       * Long collectorTimeInMillis = collectorResult.getTime();
       * List<CollectorResult> profiledChildren = collectorResult.getProfiledChildren();
       *
       * 4) 集合树分析查询
       * CollectorResult collectorResult = queryProfileResult.getCollectorResult();
       * String collectorName = collectorResult.getName();
       * Long collectorTimeInMillis = collectorResult.getTime();
       * List<CollectorResult> profiledChildren = collectorResult.getProfiledChildren();
       *
       */

      println(searchResponse)
    }

    def searchScrollApi() = ???

    def clearScrollApi() = ???

    def mutilSearchApi() = ???

    def searchTemplateApi() = ???

    def mutilSearchTemplateApi() = ???

    def fieldCapabilitiesApi() = ???

    def rankingEvaluationApi() = ???

    def explainApi() = ???

    def countApi() = ???

  }




}
