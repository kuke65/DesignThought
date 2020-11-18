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


3、
```
curl -X POST -H "Content-Type: application/json" -d '{"id": "posts" ,"secret": "dt_FAfQDrefwERw1d3f.2e2Os41fwAfe" ,"scope": "scope" ,"name":"name"  ,"created": 1605738162  ,"uri": "ops","descr": "descr" ,"ctype": 1 ,"status": 1 , "detail":{"user":"akuk"} }' http://127.0.0.1:8051/redis/regions
```

4、
浏览器访问 http://127.0.0.1:8051/redis/regions?_param={"clientId":"posts", "clientSecret":"dt_FAfQDrefwERw1d3f.2e2Os41fwAfe"}
或者
curl http://127.0.0.1:8051/redis/regions?_param=%7B%22clientId%22%3A%22posts%22%2C%20%22clientSecret%22%3A%22dt_FAfQDrefwERw1d3f.2e2Os41fwAfe%22%7D


