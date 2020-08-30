package com.jn.agileway.spring.web.rest;

import com.jn.agileway.web.rest.GlobalRestExceptionHandler;
import com.jn.langx.util.reflect.Reflects;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 该类只用于在Spring环境下的Rest方式
 */
public class GlobalSpringRestExceptionHandler extends GlobalRestExceptionHandler implements HandlerExceptionResolver {
    @Override
    protected boolean isSupportedRestAction(HttpServletRequest request, HttpServletResponse response, Object methodHandler, Exception ex) {
        if (!(methodHandler instanceof HandlerMethod)) {
            return false;
        }
        Class controllerClass = ((HandlerMethod) methodHandler).getBeanType();
        return Reflects.isAnnotationPresent(controllerClass, RestController.class);
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object methodHandler, Exception ex) {
        handle(request, response, methodHandler, ex);
        // 返回 empty ModelAndView 放置把异常写出
        return new ModelAndView();
    }
}
