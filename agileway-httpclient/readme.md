## HTTP Client对比

|              | JDK  HttpUrlConnection                | JDK11 HttpClient                                             | Apache HttpComponent          | OKHttp3                                                     | Netty HttpCodec                                        | Jetty Client                                                 |
| ------------ | ------------------------------------- | ------------------------------------------------------------ | ----------------------------- | ----------------------------------------------------------- | ------------------------------------------------------ | ------------------------------------------------------------ |
| HTTP Version | 1.1                                   | 1.1， 2                                                      | 1.1， 2                       | 1.1， 2                                                     | 1.1， 2                                                | 1.1， 2                                                      |
| Http Method  | 不支持 PATCH，不支持扩展的http method | 无限制                                                       | 无限制                        | 无限制                                                      | 无限制                                                 | 无限制                                                       |
| SSE          | 不支持，需自行实现                    | 不支持，需自行实现                                           | 支持，在扩展库中              | 原生支持`EventSource` API                                   | 不支持，但可通过`HttpObjectAggregator`处理分块传输实现 | 提供`EventSource`接口                                        |
| WebSocket    | 不支持                                | 支持原生`WebSocket`接口，支持双向通信，但需处理Ping/Pong帧和重连逻辑 | 支持，在扩展库中              | 原生`WebSocket` API，自动分帧、支持二进制数据，心跳机制完善 | 支持，通过`WebSocketServerProtocolHandler`处理协议升级 | 原生`WebSocketClient`，与Jetty服务器无缝集成，支持RFC6455协议 |
| 异步         | 不支持                                | 基于CompletableFuture                                        | 支持                          | 原生支持                                                    | 原生支持，基于event                                    | 基于NIO非阻塞                                                |
| 连接池       | 不支持                                | 内置支持                                                     | 支持，属于高级配置            | 智能复用                                                    | 需自定义实现                                           | 支持                                                         |
| 性能         | 低并发下尚可，高并发阻塞严重          | 高吞吐，多路复用减少延迟                                     | 稳定但线程模型开销较大        | 连接复用+GZIP透明压缩                                       | 极致性能（事件驱动）                                   | 高并发低延迟                                                 |
| 适用场景     | 简单请求、无外部依赖需求              | 现代Java应用、HTTP/2需求                                     | 企业级复杂功能（如代理/认证） | Android/高性能服务                                          | 自定义协议/网关开发                                    | 高并发服务/WebSocket实时通信                                 |

## agileway-httpclient 使用注意事项

+ 除了SSE外，所有的http响应都会在底层读取所有的byte后，直接关闭，
+ 当进行文件下载时，如果是小文件，可以直接使用。如果是大文件，建议要定义 HttpResponsePayloadExtractor
+