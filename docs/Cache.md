# 架构组成
主要组成框架ElasticSearch、Redis、Zookeeper、Kafka、Akka-Http
### 说明 
缓存服务ES和Redis,Kafka实现异步更新缓存,Zookeeper协调并发请求.
搭建缓存服务目的是解决对代码的松耦合,更好服务对不同区域的用户调用不同的缓存服务
或是所有订单列表缓存数据.
事件没有持久化处理,有需要的可以自行实现事件持久化
数据写入可以使用Kafka异步数据写入



## ElasticSearch
分布式搜索引擎服务,支持高效全文检索

## Redis
Key-Value NoSQL内存数据库,读写速度快

## Zookeeper
分布式服务协调服务,可以基于事件监听实现分布式锁管理

## Kafka
分布式发布订阅消息系统,异步缓存数据,也能够很好的控制ES和Redis写吞吐量




## 使用方式

启动项目
```
sbt -Dhostname=xxx.xxx.xxx.xxx "runMain com.akuk.dt.cache.Main 2551"
xxx.xxx.xxx.xxx 设置ElasticSearch服务器地址
```

1、创建索引数据
```
curl -X POST -H "Content-Type: application/json" -d '{"index":"posts", "esType":"_doc", "docId":"1", "jsonString":"{\"user\":\"akuk\"}"}' http://127.0.0.1:8051/elasticSearch/posts
```

2、获取索引数据
```
浏览器访问 http://127.0.0.1:8051/elasticSearch/regions?_param={"index":"posts", "esType":"_doc", "docId":"1", "jsonString":""}
或者
curl http://127.0.0.1:8051/elasticSearch/regions?_param=%7B%22index%22%3A%22posts%22%2C%22esType%22%3A%22_doc%22%2C%22docId%22%3A%221%22%2C%22jsonString%22%3A%22%22%7D
```


3、保存凭证信息到redis
```
curl -X POST -H "Content-Type: application/json" -d '{"id": "posts" ,"secret": "dt_FAfQDrefwERw1d3f.2e2Os41fwAfe" ,"scope": "scope" ,"name":"name"  ,"created": 1605738162  ,"uri": "ops","descr": "descr" ,"ctype": 1 ,"status": 1 , "detail":{"user":"akuk"} }' http://127.0.0.1:8051/redis/regions
```

4、校验redis凭证信息
```
浏览器访问 http://127.0.0.1:8051/redis/regions?_param={"clientId":"posts", "clientSecret":"dt_FAfQDrefwERw1d3f.2e2Os41fwAfe"}
或者
curl http://127.0.0.1:8051/redis/regions?_param=%7B%22clientId%22%3A%22posts%22%2C%20%22clientSecret%22%3A%22dt_FAfQDrefwERw1d3f.2e2Os41fwAfe%22%7D
```













