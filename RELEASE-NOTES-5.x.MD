## 5.0.5 （进行中）

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