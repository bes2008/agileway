package com.jn.agileway.feign.supports.rpc;

import com.jn.agileway.feign.ErrorHandler;
import feign.InvocationHandlerFactory;

/**
 * @since 2.6.0
 */
public interface FeignRpcErrorHandler extends ErrorHandler<FeignRpcException> {
    @Override
    Object apply(FeignRpcException e, InvocationHandlerFactory.MethodHandler methodHandler);
}
