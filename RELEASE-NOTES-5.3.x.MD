## 5.3.1
+ agileway-httpclient：支持 SSE
+ agileway-spring: fix spring.factories

## 5.3.0
+ 移除 agileway-feign，不再支持 feign，由 agileway-httpclient 替代
+ 单元测试框架更换: junit4 => junit5 
+ agileway-httpclient 支持 配置 HttpRequestPayloadTransformer，用于支持 WS-Security 
+ 依赖升级：
  + jetty: 12.0.24 => 12.1.0 