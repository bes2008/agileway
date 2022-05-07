package com.jn.agileway.redis.examples.controller.redis_examples;

import com.jn.agileway.http.rest.RestActionException;
import com.jn.agileway.http.rest.RestActionExceptions;
import com.jn.agileway.web.rest.AbstractServletRestActionExceptionHandler;
import com.jn.langx.http.rest.RestRespBody;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RestActionExceptions({
        @RestActionException(MyCustomizException.class),
})
public class MyCustomizExceptionHandler extends AbstractServletRestActionExceptionHandler {
    @Override
    public RestRespBody handle(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        return null;
    }
}
