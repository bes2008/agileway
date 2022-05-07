package com.jn.agileway.springboot.web.rest.exceptionhandler;

import com.jn.agileway.http.rest.RestActionException;
import com.jn.agileway.http.rest.RestActionExceptionHandler;
import com.jn.agileway.http.rest.RestActionExceptions;
import com.jn.agileway.web.rest.AbstractServletRestActionExceptionHandler;
import com.jn.langx.http.rest.RestRespBody;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RestActionExceptions({
        @RestActionException(ConversionNotSupportedException.class),
        @RestActionException(ConversionNotSupportedException.class),
        @RestActionException(HttpMessageNotWritableException.class),
        @RestActionException(MissingServletRequestPartException.class)
})
public class SpringServerExceptionHandler extends AbstractServletRestActionExceptionHandler<String> {
    @Override
    public RestRespBody<String> handle(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        request.setAttribute("javax.servlet.error.exception", ex);
        return RestRespBody.error500("HTTP-500", ex.getMessage());
    }
}
