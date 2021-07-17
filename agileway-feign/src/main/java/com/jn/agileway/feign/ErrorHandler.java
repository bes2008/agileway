package com.jn.agileway.feign;

import com.jn.langx.util.function.Function;

public interface ErrorHandler<E extends Throwable> extends Function<E, Object> {
    Object handle(E exception);
}
