package com.jn.agileway.feign.supports.rpc;

import com.jn.agileway.feign.ErrorHandler;

public interface FeignRPCExceptionHandler extends ErrorHandler<FeignRPCException> {
    @Override
    Object handle(FeignRPCException exception);
}
