package com.jn.agileway.springboot.web.rest.exceptionhandler;

import com.jn.agileway.web.rest.RestActionException;
import com.jn.agileway.web.rest.RestActionExceptionHandler;
import com.jn.agileway.web.rest.RestActionExceptions;
import com.jn.langx.http.rest.RestRespBody;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpMediaTypeNotAcceptableException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RestActionExceptions({
        @RestActionException(HttpMediaTypeNotAcceptableException.class)
})
public class HttpMediaTypeNotAcceptableExceptionHandler implements RestActionExceptionHandler<String> {
    @Override
    public RestRespBody<String> handle(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) {
        if (!(exception instanceof HttpMediaTypeNotAcceptableException)) {

        }
        return RestRespBody.error(HttpServletResponse.SC_NOT_ACCEPTABLE, "HTTP-406", "media type 不可接受");
    }
}
