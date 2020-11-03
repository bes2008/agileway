package com.jn.agileway.springboot.web.rest.exceptionhandler;


import com.jn.agileway.web.rest.RestActionException;
import com.jn.agileway.web.rest.RestActionExceptionHandler;
import com.jn.agileway.web.rest.RestActionExceptions;
import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Strings;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RestActionExceptions({
        @RestActionException(HttpRequestMethodNotSupportedException.class)
})
public class HttpRequestMethodNotSupportedExceptionHandler implements RestActionExceptionHandler<String> {
    @Override
    public RestRespBody<String> handle(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) {
        if (!(exception instanceof HttpRequestMethodNotSupportedException)) {

        }
        HttpRequestMethodNotSupportedException ex = (HttpRequestMethodNotSupportedException) exception;
        RestRespBody<String> respBody = RestRespBody.error(405, "HTTP-405", null);
        if (Emptys.isNotEmpty(ex.getSupportedMethods())) {
            String supportedMethods = Strings.join(", ", ex.getSupportedMethods());
            response.setHeader("Allow", supportedMethods);
            respBody.setErrorMessage("支持的HTTP Method有：" + supportedMethods);
        }

        return respBody;
    }
}
