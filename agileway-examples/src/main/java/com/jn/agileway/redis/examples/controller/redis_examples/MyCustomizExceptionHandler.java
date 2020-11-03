package com.jn.agileway.redis.examples.controller.redis_examples;

import com.jn.agileway.web.rest.RestActionException;
import com.jn.agileway.web.rest.RestActionExceptionHandler;
import com.jn.agileway.web.rest.RestActionExceptions;
import com.jn.langx.http.rest.RestRespBody;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RestActionExceptions({
        @RestActionException(MyCustomizException.class),
})
public class MyCustomizExceptionHandler implements RestActionExceptionHandler {
    @Override
    public RestRespBody handle(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        return null;
    }
}
