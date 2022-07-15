[![License](https://img.shields.io/badge/license-Apach2.0-green.svg)](https://github.com/fangjinuo/agileway/blob/master/LICENSE)
[![maven](https://img.shields.io/badge/maven-v3.1.3-green.svg)](https://search.maven.org/search?q=g:com.github.fangjinuo.agileway%20AND%20v:3.1.3)



# agileway
Java、Web开发工具套件，尽量避免996

## module & features
+ [agileway-codec: 提供通用的encode, decode实现](./.wiki/agileway-codec.MD)
    + 支持基于 Activej 序列化规范的实现
    + 支持基于 Avro 序列化规范的实现
    + 支持基于 bson 序列化规范的实现
    + 支持基于 Cbor 序列化规范的实现
    + 支持基于 FSE 序列化框架实现
    + 支持基于 FST 序列化框架实现
    + 支持基于 hessian 序列化框架实现
    + 支持基于 Jdk Serializable 规范实现
    + 支持基于 easyjson, jackson实现
    + 支持基于 Kryo 序列化框架实现
    + 支持基于 MsgPack 序列化规范的实现
    + 支持基于 Protostuff 序列化框架实现
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
        + WAF (Web Application Firewall)
           + XSS
           + CSRF
           + SQL Inject
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
+ agileway-jdbc-datasource: 提供统一数据源 （已迁移至 sqlhelper）
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
+ agileway-aop: 对AOP的支持、适配    
+ agileway-sshclient:
    + 功能
      + 支持 Session Channel
      + 支持 forwarding
      + 支持 SCP
      + 支持 SFTP
    + 类库
      + com.jcraft:jsch:0.1.55
      + com.trilead:trilead-ssh2:1.0.0-build222
      + com.airlenet.yang:ganymed-ssh2:1.2.0
      + net.schmizz:sshj:0.10.0
      + sshtools:j2ssh-core:0.2.9
+ agileway-vfs: 
    + 对commons-vfs 进行二次封装
    + 支持 agileway-sshclient
    + 提供统一的 artifact管理抽象
+ agileway-zip
    + 对commons-compress 进行二次封装，简化使用      

| module | jdk | version |
|---|---|---|
|agileway-web|7|1.0.0+|
|agileway-spring|7|1.0.0+|
|agileway-feign|6|1.0.0+|
|agileway-codec|7|1.1.0+|
|agileway-redis|7|1.1.0+|
|agileway-shiro-redis|7|1.1.0+|
|agileway-dmmq|7|1.2.0+|
|agileway-httpclient|6|1.3.0+|
|agileway-aop|6|2.1.0+|
|agileway-sshclient|6|2.3.0+|
 



             