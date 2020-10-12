## Agileway-Redis

该模块是基于 spring-data-redis 开发的。提供了如下功能：

+ 提供Java集合的扩展：支持了 List, Map, Set, SortedSet, Stack等接口
+ 提供分布式Counter，提供了基于string, hash两种类型的Counter
+ 提供分布式Cache : 实现的是 com.jn.langx.cache.Cache 接口
+ 提供分布式Lock：实现了 java.util.concurrent.locks.Lock 接口
+ 提供RedisTemplate 全局 Key Prefix
+ 支持基于agileway-codec来使用多种序列化框架   
+ 支持注册自定义Lua脚本

## 在Spring Boot环境下使用agileway-redis

在agileway-spring模块里，提供了基于springboot 下的AutoConfigurer。
所以如果在spring-boot环境下，需要引入agileway-srping模块，并在application.yml中做如下的配置：

在agileway-spring里，为redis提供了全局的RedisTemplate:

配置如下：

```yaml
agileway:
  redis:
    global-template:
      enabled: true
      key:
        prefix: agileway_redis_test
```

Redis 连接等配置，仍然使用Spring-Boot-Starter-Data-Redis （实际代码是在spring-boot.jar）中提供的。




