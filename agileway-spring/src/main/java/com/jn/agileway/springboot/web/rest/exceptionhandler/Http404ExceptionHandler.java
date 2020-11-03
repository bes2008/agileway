package com.jn.agileway.springboot.web.rest.exceptionhandler;

import com.jn.agileway.web.rest.RestActionException;
import com.jn.agileway.web.rest.RestActionExceptionHandler;
import com.jn.agileway.web.rest.RestActionExceptions;
import com.jn.langx.http.rest.RestRespBody;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RestActionExceptions({
        @RestActionException(NoHandlerFoundException.class)
})
public class Http404ExceptionHandler implements RestActionExceptionHandler<String> {
    @Override
    public RestRespBody<String> handle(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        return RestRespBody.error404("HTTP-404", ex.getMessage());
    }
}
