package com.jn.agileway.springboot.web.rest.exceptionhandler;


import com.jn.agileway.http.rest.RestActionException;
import com.jn.agileway.http.rest.RestActionExceptions;
import com.jn.agileway.web.filter.waf.allowedmethods.HttpMethodNotSupportedException;
import com.jn.agileway.web.rest.AbstractServletRestActionExceptionHandler;
import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Strings;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RestActionExceptions({
        @RestActionException(HttpMethodNotSupportedException.class),
})
public class HttpMethodNotSupportedExceptionHandler extends AbstractServletRestActionExceptionHandler<String> {
    @Override
    public RestRespBody<String> handle(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) {
        HttpMethodNotSupportedException ex = (HttpMethodNotSupportedException) exception;
        RestRespBody<String> respBody = RestRespBody.error(405, "HTTP-405", null);
        if (Emptys.isNotEmpty(ex.getSupportedMethods())) {
            String supportedMethods = Strings.join(", ", ex.getSupportedMethods());
            response.setHeader("Allow", supportedMethods);
            respBody.setErrorMessage(StringTemplates.formatWithPlaceholder("unsupported HTTP method: {}, supported HTTP methods：[{}]", ex.getMethod(), supportedMethods));
        }

        return respBody;
    }
}