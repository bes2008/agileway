package com.jn.agileway.springboot.web.rest.exceptionhandler;

import com.jn.agileway.http.rest.RestActionException;
import com.jn.agileway.http.rest.RestActionExceptions;
import com.jn.agileway.web.rest.AbstractServletRestActionExceptionHandler;
import com.jn.langx.http.rest.RestRespBody;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.multipart.MultipartException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@RestActionExceptions({
        @RestActionException(MissingServletRequestParameterException.class),
        @RestActionException(ServletRequestBindingException.class),
        @RestActionException(TypeMismatchException.class),
        @RestActionException(HttpMessageNotReadableException.class),
        @RestActionException(MethodArgumentNotValidException.class),
        @RestActionException(BindException.class),
        @RestActionException(MultipartException.class)
})
public class Http400ExceptionHandler extends AbstractServletRestActionExceptionHandler<String> {
    @Override
    public RestRespBody<String> handle(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        return RestRespBody.error400("HTTP-400", ex.getMessage());
    }
}
