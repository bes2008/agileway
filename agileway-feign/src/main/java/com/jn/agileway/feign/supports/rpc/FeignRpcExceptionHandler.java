package com.jn.agileway.feign.supports.rpc;

import com.jn.agileway.feign.ErrorHandler;

public interface FeignRpcExceptionHandler extends ErrorHandler<FeignRpcException> {
    @Override
    Object handle(FeignRpcException exception);
}
