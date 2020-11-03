package com.jn.agileway.springboot.web.rest.exceptionhandler;

import com.jn.agileway.web.rest.RestActionException;
import com.jn.agileway.web.rest.RestActionExceptionHandler;
import com.jn.agileway.web.rest.RestActionExceptions;
import com.jn.langx.http.rest.RestRespBody;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestActionExceptions({
        @RestActionException(ConversionNotSupportedException.class),
        @RestActionException(ConversionNotSupportedException.class),
        @RestActionException(HttpMessageNotWritableException.class),
        @RestActionException(MissingServletRequestPartException.class)
})
public class SpringServerExceptionHandler implements RestActionExceptionHandler<String> {
    @Override
    public RestRespBody<String> handle(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        request.setAttribute("javax.servlet.error.exception", ex);
        return RestRespBody.error500("HTTP-500", ex.getMessage());
    }
}
