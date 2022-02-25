package com.jn.agileway.springboot.web.rest.exceptionhandler;

import com.jn.agileway.web.rest.RestActionException;
import com.jn.agileway.web.rest.RestActionExceptions;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;

@Component
@RestActionExceptions({
        @RestActionException(HttpSessionRequiredException.class),
        @RestActionException(ServletRequestBindingException.class),
        @RestActionException(TypeMismatchException.class),
        @RestActionException(HttpMessageNotReadableException.class),
        @RestActionException(MethodArgumentNotValidException.class),
        @RestActionException(BindException.class)
})
public class Http401ExceptionHandler {
}
