[![License](https://img.shields.io/badge/license-Apach2.0-green.svg)](https://github.com/fangjinuo/agileway/blob/master/LICENSE)
[![maven](https://img.shields.io/badge/maven-v1.4.1-green.svg)](https://search.maven.org/search?q=g:com.github.fangjinuo.sqlhelper%20AND%20v:1.4.1)



# agileway
Java、Web开发工具套件，尽量避免996

## module & features
+ [agileway-codec: 提供通用的encode, decode实现](./.wiki/agileway-codec.MD)
    + 支持基于 Jdk Serializable 规范实现
    + 支持基于 easyjson, jackson实现
    + 支持基于 hessian 序列化框架实现
    + 支持基于 Kryo 序列化框架实现
    + 支持基于 Protostuff 序列化框架实现
    + 支持基于 FST 序列化框架实现
    + 支持基于 FSE 序列化框架实现
    + 支持基于 XSON 序列化框架实现
+ [agileway-feign： 提供对feign的扩展](./.wiki/agileway-feign.MD)
    + 提供基于 Feign 的 RestService动态创建
    + 提供基于 easyjson 的 encoder, decoder
    + 集成负载均衡功能
    + 提供多种 param expander
+ [agileway-web: 提供对Servlet规范的扩展](./.wiki/agileway-web.MD)
    + filters: 
        + access log filter
        + encoding filter
        + rr filter
        + global response filter
    + rest: 
        + 提供统一的异常处理
        + 提供rest 请求结果统一化结构处理机制
    + servlets:
        + 基于rr filter， 提供request parameter 访问器
        + 提供request header, response header 处理
        + 提供断点续传下载    
+ agileway-spring: 
    + web:
        + rest: 提供统一结果、统一异常的Spring环境下的处理
    + springboot:
        + web:
            + rest: 提供统一结果、统一异常的Spring-Boot环境下的处理
        + redis: 提供redis 在Spring-boot下的全局RedisTemplate
+ agileway-jdbc-datasource: 提供统一数据源
    + 支持 dbcp2
    + 支持 c3p0
    + 支持 druid
    + 支持 hikaricp
    + 支持 tomcat-jdbc
+ [agileway-redis: 基于 RedisTemplate 提供一些扩展](./.wiki/agileway-redis.MD)
    + 提供Java集合的扩展
    + 提供分布式Counter
    + 提供分布式Cache
    + 提供分布式Lock
    + 提供RedisTemplate 全局 Key Prefix
    + 支持基于agileway-codec来使用多种序列化框架        
    + 支持注册自定义Lua脚本
+ agileway-shiro-redis: 提供Shiro基于Redis的Cache，Session实现
    + 提供基于redis的Shiro Cache实现
    + 提供基于redis的Shiro Session访问       
+ [agileway-dmmq: 基于Disruptor实现的Memory Message Queue](./agileway-dmmq/README.MD)
    + 消息流转：producer -> topic -> consumer
    + 一个producer可以push消息到多个topic, 一个topic可以接收多个producer发的消息
    + 一个consumer可以从多个topic拉取消息， 一个topic的消息可以被多个消费者重复的消费
    + 一个consumer可以依赖于从同一topic拉取消息的多个其他的consumer
    
             