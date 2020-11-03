该项目由CQRS+Event Source原型思想设计进行开发的开源项目，仅贡献给大家学习、参考

有想法的伙伴可以联系本人邮箱635335061@qq.com

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



