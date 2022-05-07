package com.jn.agileway.springboot.web.rest.exceptionhandler;

import com.jn.agileway.http.rest.RestActionException;
import com.jn.agileway.http.rest.RestActionExceptions;
import com.jn.agileway.web.rest.AbstractServletRestActionExceptionHandler;
import com.jn.langx.Ordered;
import com.jn.langx.http.rest.RestRespBody;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RestActionExceptions(
        value = {
            @RestActionException(Throwable.class)
        },
        order = Ordered.LOWEST_PRECEDENCE
)
public class ThrowableHandler extends AbstractServletRestActionExceptionHandler<String> {
    @Override
    public RestRespBody<String> handle(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        return RestRespBody.error500("HTTP-500", ex.getMessage());
    }
}
