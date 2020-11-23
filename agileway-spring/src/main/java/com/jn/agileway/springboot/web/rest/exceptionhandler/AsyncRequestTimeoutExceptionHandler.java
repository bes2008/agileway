package com.jn.agileway.springboot.web.rest.exceptionhandler;

import com.jn.agileway.web.rest.RestActionException;
import com.jn.agileway.web.rest.RestActionExceptionHandler;
import com.jn.agileway.web.rest.RestActionExceptions;
import com.jn.langx.http.rest.RestRespBody;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RestActionExceptions({
        @RestActionException(AsyncRequestTimeoutException.class),
})
public class AsyncRequestTimeoutExceptionHandler implements RestActionExceptionHandler<String> {
    @Override
    public RestRespBody<String> handle(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        return RestRespBody.error503("HTTP-503", ex.getMessage());
    }
}