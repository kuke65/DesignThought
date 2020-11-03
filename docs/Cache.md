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










