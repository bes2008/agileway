package com.jn.agileway.feign.supports.rpc;

import com.jn.agileway.feign.ErrorHandler;
import feign.InvocationHandlerFactory;

public interface FeignRpcExceptionHandler extends ErrorHandler<FeignRpcException> {
    @Override
    Object apply(FeignRpcException e, InvocationHandlerFactory.MethodHandler methodHandler);
}
