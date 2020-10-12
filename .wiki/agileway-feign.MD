## Agileway-Feign
相信很多人在第一次接触feign，应该是了解Spring Cloud技术栈开始的，我也不例外。
但是网上有很多甚至区分了feign, open-feign的文章，甚至把feign定性为Spring开发团队为Spring Cloud开发的技术的一部分，这是大错特错的。

在使用Feign之前，我们可能用到的比较多的是RestTemplate，从用了Feign后，开发的工作量极大的减轻。但大家目前都是在Spring Cloud环境下才用的它，
那么多非Spring Cloud的应用中，怎么方便的使用呢。agileway-feign就是为了解决这个问题的。

## Features

+ 提供基于 Feign 的 RestService动态创建
+ 支持统一的返回结果，也可以自定义统一返回结果类型
+ 提供基于 easyjson 的 encoder, decoder
+ 集成负载均衡功能
+ 提供多种 param expander


## Core API

```java
RestServiceProvider#getSerice(Class serviceStubClass)
```


## 用例：

1） 客户端接口
```java

@Headers({
        "Content-Type: application/json",
        "Accept: */*",
        "Connection: keep-alive"
})
public interface ConsumerStub {
    @RequestLine("POST /consumers")
    @Body("{body}")
    RestRespBody<Boolean> register(@Param(value = "body", expander = ToJsonStringExpander.class) Consumer consumer);

    @RequestLine("DELETE /consumers")
    @Body("{body}")
    void unregister(@Param(value = "body", expander = ToJsonStringExpander.class) Consumer consumer);
}


```

2） 客户端服务接口初始化

```java

RestServiceProvider serviceProvider = new RestServiceProvider();
serviceProvider.setContext(loaderContext);
serviceProvider.setJsonFactory(JsonFactorys.getJSONFactory(JsonScope.PROTOTYPE));
serviceProvider.setUnifiedRestResponseClass(RestRespBody.class);
serviceProvider.init();

```



3）方法调用

```java
Consumer consumer = buildConsumer();
ConsumerStub consumerStub = serviceProvider.getService(ConsumerStub.class);
if (!consumerStub.register(consumer).getData()) {
    logger.error("Failure to register consumer {} to license server {}", consumer.toString(), loaderContext.getNodes());
    return null;
}

```



