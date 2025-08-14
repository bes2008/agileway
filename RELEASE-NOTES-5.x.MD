## 5.2.4
+ 移除 agileway-sshclient-j2ssh 模块
+ 完善 agileway-sshclient-jsch, 支持默认的 Config
+ 支持 sshclient 一次性创建多级目录
+ 优化 loadbalancer 模块
  + 支持自定义负载均衡算法
  + 支持自定义节点检查
  + 支持自定义节点提供商
+ 依赖变更：
  + 升级 maverick-synergy-client：3.0.1 => 3.1.0
  + 升级 eddsa: 0.3.0 => 0.3.1
  + 升级 commons-lang3: 3.17.0 -> 3.18.0
  + 升级 commons-compress: 1.27.1 -> 1.28.0
  + 升级 commons-vfs: 2.2.0 => 2.10.0
  + 升级 jackson: 2.13.5 => 2.15.3
  + 升级 snakeyaml: 1.33 => 2.0
  + 升级 zookeeper: 3.9.2 => 3.9.3
  + 升级 curator: 5.8.0 => 5.9.0
  + 升级 logback: 1.5.0 => 1.5.18
  + 升级 spring: 6.0.14 => 6.1.21
  + 升级 spring-boot: 3.1.6 => 3.3.13
  + 升级 spring-data-redis: 2.3.4 => 3.5.2


## 5.2.3
+ 提供 agileway-httpclient-jetty模块
+ 增强 agileway-eipchannel-core模块，提供 P2pRouter, PubSubRouter

## 5.2.2
+ 增强 agileway-httpclient:
  + 支持基于http protocol version 选择对应的 http request executor
  + 支持基于JDK 版本选择对应的 http request executor
  + 支持设置请求的timeout
+ 完善 agileway-httpclient-jdk11 文件上传功能
+ 完善 agileway-oauth2-client-springboot-starter，支持autoconfiguration
+ 完善 agileway-shell 模块的 HistoryHandler

## 5.2.1
+ 优化 jwt模块，支持从 Payload 直接获取 默认注册的 claims
+ 提供 oauth2-client 模块
+ 增强 httpclient declarative 模块的注解数据赋值能力
+ 优化 httpclient-okhttp：
  + 对于 POST 等方法，没有 body 时，使用 new byte[0]
+ 优化 根pom.xml
+ 提取 agileway-httpclient-auth模块

## 5.2.0
+ 升级 Spring 6.x
+ 升级 Spring Boot 3.x
+ 支持 httpclient 模块
  + 升级 JDK 11 之前的 HttpURLConnection
  + 支持 JDK 11 HttpClient
  + 支持 OkHttp3
  + 支持 apache httpcomponents
  + 支持 SOAP
  + 支持 @HttpEndpoint
+ 将 langx-java-distributed 模块迁移为 agileway-distributed模块
+ 提供 agileway-zookeeper模块 
+ 对agileway-jwt 模块进行优化，确保签名验证时，使用的是接收到的数据

## 5.1.0
+ 新特性：
  + 提供 agileway-shell：用于支持 CLI 程序开发
+ 依赖：langx-java: 5.4.5 -> 5.4.6


## 5.0.4
+ BugFix：
  + 避免byte[]的返回值被转换为Base64格式
  
## 5.0.3
+ 新特性：
   + Sftps 支持直接copy 文件流到远程
   + sshj 支持 shell executor
+ 依赖：
  + 升级 avro：1.11.0 -> 1.11.3
  + 升级 sshj：0.32.0 -> 0.38.0
  + 升级 bcprov-jdk15on:1.70 -> bcprov-jdk18on:1.78.1
  + 升级 commons-text:1.8 -> 1.12.0
  + 升级 commons-configuration:1.8 -> 1.10
  + 升级 guava: 20.0 -> 33.2.1-jre
  + 升级 jersey: 2.35 -> 2.44
  + 升级 nimbus-jose-jwt: 9.37 -> 9.40
  + 升级 shiro: 1.11.0 -> 2.0.1
  + 升级 zip4j: 2.9 -> 2.11.5
  + 升级 jackson: 2.13.1 -> 2.13.5
  + 升级 spring5: 5.3.27 -> 5.3.37
  + 升级 spring-boot2: 2.7.12 -> 2.17.18
  + 升级 sqlhelper: 5.0.0 -> 5.0.1
  + 升级 langx: 5.4.0 -> 5.4.1
+ 模块管理：
  + 移除 agileway-eipchannel-kafka

## 5.0.2
1. 增强 jwt，支持规范中指定的 内置的claim names

## 5.0.1
1. 移除模块 agileway-redis (原因：不再支持spring-data-redis 1.x版本)
2. 移除模块 agileway-shiro-redis (原因：不再支持spring-data-redis 1.x版本)

## 5.0.0
1. 升级langx-java：5.4.0
2. agileway全面升级JDK 1.8