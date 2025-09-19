## 5.3.2
+ Features:
  + agileway-codec
    + 支持多种 xml 编排： jaxb, simple-xml, xstream 等
  + agileway-httpclient: 
    + 提供 HttpRequestBuilder
    + SSE 支持自定义 HttpRequest，例如可自定义为 POST请求
    + 提供通用的 text 类型的请求响应读写转换
    + 提供通用的 application/xml 类型的请求响应读写转换
    + 支持自定义 CredentialsInjector
+ BugFix:
  + agileway-httpclient: 
    + fix sse reconnect interval invalid
    + avoid customized transformer failed

## 5.3.1
+ Features:
  + agileway-httpclient：支持 SSE
+ BugFix:
  + agileway-spring: fix spring.factories
+ Dependency Updates：
  + langx-java: 5.5.7 => 5.5.8 (SSE required)

## 5.3.0
+ Features:
  + agileway-httpclient: 支持配置 HttpRequestPayloadTransformer，用于支持 WS-Security
  + 移除 agileway-feign，不再支持 feign，由 agileway-httpclient 替代
+ Tests:
  + 单元测试框架更换: junit4 => junit5 
+ Dependency Updates：
  + jetty: 12.0.24 => 12.1.0 