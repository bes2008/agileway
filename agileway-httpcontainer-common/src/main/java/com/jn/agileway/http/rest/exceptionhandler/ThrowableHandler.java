package com.jn.agileway.http.rest.exceptionhandler;

import com.jn.agileway.http.rest.RestActionException;
import com.jn.agileway.http.rest.RestActionExceptionHandler;
import com.jn.agileway.http.rest.RestActionExceptions;
import com.jn.agileway.http.rr.HttpRequest;
import com.jn.agileway.http.rr.HttpResponse;
import com.jn.langx.Ordered;
import com.jn.langx.http.rest.RestRespBody;

@RestActionExceptions(
        value = {
            @RestActionException(Throwable.class)
        },
        order = Ordered.LOWEST_PRECEDENCE
)
public final class ThrowableHandler implements RestActionExceptionHandler<String> {
    @Override
    public RestRespBody<String> handle(HttpRequest request, HttpResponse response, Object handler, Exception ex) {
        return RestRespBody.error500("HTTP-500", ex.getMessage());
    }
}
