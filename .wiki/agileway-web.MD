## agileway-web

# 统一返回值
不论是正常，还是异常情况，都提供统一返回值：

```java

public final class RestRespBody<T> {
    private boolean success;
    private T data;
    private int statusCode;
    private long timestamp;
    private String errorCode;
    private String errorMessage;

}
```

结构描述：

```yaml
{
  "success": false, # 是否成功
  "statusCode": 400, # http 响应状态码
  "timestamp": 1231231231423, # 时间戳
  "errorCode": "UM-xxx0" , # 错误码
  "errorMessage": "请求参数错误", # 错误内容
  "data": "返回的数据"

}
```

## 统一的异常处理
Spring 中虽然提供了统一的异常处理（基于@ControllerAdvice），但仍然有不能够处理的情况：
1）有人喜欢对Exception做二次包装，例如包装为RuntimeException
2）只能处理Controller层的异常，对于Filter中的处理不了
3）只能用于Spring应用


## 在Spring Boot 应用中启用统一异常处理、统一响应功能

在SpringBoot应用中如何启用?
1）引入 agileway-spring
2）在启动类上添加 @EnableGlobalRestHandlers 注解
3）在application.yml中做如下配置：

```yaml
agileway:
  rest:
    global-response-body:
      base-packages:
        - com.bes.license.server.bootstrap.controller
```



