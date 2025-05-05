package com.jn.agileway.springboot.web.rest.exceptionhandler;

import com.jn.agileway.http.rest.RestActionException;
import com.jn.agileway.http.rest.RestActionExceptions;
import com.jn.agileway.web.rest.AbstractServletRestActionExceptionHandler;
import com.jn.langx.http.rest.RestRespBody;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpMediaTypeNotAcceptableException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@RestActionExceptions({
        @RestActionException(HttpMediaTypeNotAcceptableException.class)
})
public class HttpMediaTypeNotAcceptableExceptionHandler extends AbstractServletRestActionExceptionHandler<String> {
    @Override
    public RestRespBody<String> handle(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) {
        return RestRespBody.error(HttpServletResponse.SC_NOT_ACCEPTABLE, "HTTP-406", "media type 不可接受");
    }
}
