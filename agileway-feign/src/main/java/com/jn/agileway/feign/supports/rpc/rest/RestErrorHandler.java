package com.jn.agileway.feign.supports.rpc.rest;

import com.jn.agileway.feign.supports.rpc.FeignRpcErrorHandler;
import com.jn.agileway.feign.supports.rpc.FeignRpcException;
import feign.InvocationHandlerFactory;

public class RestErrorHandler implements FeignRpcErrorHandler {
    @Override
    public Object apply(FeignRpcException e, InvocationHandlerFactory.MethodHandler methodHandler) {
        return null;
    }
}
