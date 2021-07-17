package com.jn.agileway.feign;

import com.jn.langx.util.function.Function2;
import feign.InvocationHandlerFactory;

/**
 * 处理错误
 *
 * @since 2.6.0
 * @param <E>
 *
 *
 */
public interface ErrorHandler<E extends Throwable> extends Function2<E, InvocationHandlerFactory.MethodHandler, Object> {
    @Override
    Object apply(E e, InvocationHandlerFactory.MethodHandler methodHandler);
}
