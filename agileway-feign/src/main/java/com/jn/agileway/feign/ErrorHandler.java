package com.jn.agileway.feign;

import com.jn.langx.util.function.Function2;
import feign.InvocationHandlerFactory;
import feign.codec.Decoder;

/**
 * 处理错误
 *
 * @since 2.6.0
 * @param <E>
 *
 *
 */
public interface ErrorHandler<E extends Throwable> extends Function2<E, InvocationHandlerFactory.MethodHandler, Object> {

    Decoder getDecoder();
    void setDecoder(Decoder decoder);

    @Override
    Object apply(E e, InvocationHandlerFactory.MethodHandler methodHandler);
}
