package com.jn.agileway.springboot.web.rest.exceptionhandler;

import com.jn.agileway.web.rest.RestActionException;
import com.jn.agileway.web.rest.RestActionExceptionHandler;
import com.jn.agileway.web.rest.RestActionExceptions;
import com.jn.langx.http.rest.RestRespBody;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestActionExceptions({
        @RestActionException(MissingServletRequestParameterException.class),
        @RestActionException(ServletRequestBindingException.class),
        @RestActionException(TypeMismatchException.class),
        @RestActionException(HttpMessageNotReadableException.class),
        @RestActionException(MethodArgumentNotValidException.class),
        @RestActionException(BindException.class)
})
public class Http400ExceptionHandler implements RestActionExceptionHandler<String> {
    @Override
    public RestRespBody<String> handle(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        return RestRespBody.error400("HTTP-400", ex.getMessage());
    }
}
