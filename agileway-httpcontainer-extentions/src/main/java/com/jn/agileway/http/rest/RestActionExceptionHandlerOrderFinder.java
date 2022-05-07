package com.jn.agileway.http.rest;

import com.jn.langx.util.function.Supplier;

public interface RestActionExceptionHandlerOrderFinder extends Supplier<RestActionExceptionHandler, Integer> {
    @Override
    Integer get(RestActionExceptionHandler handler);
}
