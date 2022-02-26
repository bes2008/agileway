package com.jn.agileway.web.rest;

import com.jn.langx.Ordered;
import com.jn.langx.annotation.Order;
import com.jn.langx.util.reflect.Reflects;

public class DefaultRestActionExceptionHandlerOrderFinder implements RestActionExceptionHandlerOrderFinder {
    @Override
    public Integer get(RestActionExceptionHandler handler) {
        Integer order = doGet(handler);
        if (order == null) {
            order = 0;
        }
        return order;
    }

    protected Integer doGet(RestActionExceptionHandler handler) {
        // 找注解 @Order
        Integer order = null;
        Class klass = handler.getClass();
        if (handler instanceof Ordered) {
            order = ((Ordered) handler).getOrder();
        }
        if (order == null && Reflects.hasAnnotation(klass, Order.class)) {
            Order e = Reflects.getAnnotation(klass, Order.class);
            order = e.value();
        }
        if (order == null && Reflects.hasAnnotation(klass, RestActionExceptions.class)) {
            RestActionExceptions e = Reflects.getAnnotation(klass, RestActionExceptions.class);
            order = e.order();
        }
        return order;
    }
}
