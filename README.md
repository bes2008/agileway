# agileway
Java、Web开发工具套件，尽量避免996

## module
+ agileway-codec: 提供通用的encode, decode实现
    + 支持基于 Jdk Serializable 规范实现
    + 支持基于 easyjson, jackson实现
    + 支持基于 hessian 序列化框架实现
    + 支持基于 Kryo 序列化框架实现
    + 支持基于 Protostuff 序列化框架实现
+ agileway-feign： 提供对feign的扩展
    + 提供基于 Feign 的 RestService动态创建
    + 提供基于 easyjson 的 encoder, decoder
    + 集成负载均衡功能
    + 提供多种 param expander
+ agileway-web: 提供对Servlet规范的扩展
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
        rest: 提供统一结果、统一异常的Spring环境下的处理
+ agileway-jdbc-datasource: 提供统一数据源
+ agileway-redis: 基于 RedisTemplate 提供一些扩展
    + 提供Java集合的扩展
    + 提供分布式计数器
    + 提供分布式Cache
    + 提供分布式Lock
    + 提供RedisTemplate 全局 Key Prefix
    + 提供多种序列化类        
+ agileway-shiro-redis:
    + 提供基于redis的Shiro Cache实现
    + 提供基于redis的Shiro Session访问                