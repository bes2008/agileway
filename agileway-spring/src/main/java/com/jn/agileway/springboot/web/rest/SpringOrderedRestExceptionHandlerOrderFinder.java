package com.jn.agileway.springboot.web.rest;

import com.jn.agileway.http.rest.DefaultRestActionExceptionHandlerOrderFinder;
import com.jn.agileway.http.rest.RestActionExceptionHandler;
import com.jn.agileway.http.rest.RestActionExceptions;
import com.jn.langx.Ordered;
import com.jn.langx.annotation.Order;
import com.jn.langx.util.reflect.Reflects;

public class SpringOrderedRestExceptionHandlerOrderFinder extends DefaultRestActionExceptionHandlerOrderFinder {
    @Override
    protected Integer doGet(RestActionExceptionHandler handler) {
        // 找注解 @Order
        Integer order = null;
        Class klass = handler.getClass();
        if (handler instanceof Ordered) {
            order = ((Ordered) handler).getOrder();
        }

        if (order == null && Reflects.isSubClass(org.springframework.core.Ordered.class, klass)) {
            order = ((org.springframework.core.Ordered) handler).getOrder();
        }

        if (order == null && Reflects.hasAnnotation(klass, Order.class)) {
            Order e = Reflects.getAnnotation(klass, Order.class);
            order = e.value();
        }

        if (order == null && Reflects.hasAnnotation(klass, org.springframework.core.annotation.Order.class)) {
            org.springframework.core.annotation.Order e = Reflects.getAnnotation(klass, org.springframework.core.annotation.Order.class);
            order = e.value();
        }

        if (order == null && Reflects.hasAnnotation(klass, RestActionExceptions.class)) {
            RestActionExceptions e = Reflects.getAnnotation(klass, RestActionExceptions.class);
            order = e.order();
        }
        return order;
    }

}
