package com.jn.agileway.web.rest;

import com.jn.langx.util.function.Supplier;

public interface RestActionExceptionHandlerOrderFinder extends Supplier<RestActionExceptionHandler, Integer> {
    @Override
    Integer get(RestActionExceptionHandler handler);
}
