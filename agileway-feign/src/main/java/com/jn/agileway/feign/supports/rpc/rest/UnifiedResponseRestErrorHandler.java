package com.jn.agileway.feign.supports.rpc.rest;

import com.jn.agileway.feign.ErrorHandler;
import com.jn.agileway.feign.Feigns;
import com.jn.agileway.feign.supports.rpc.FeignRpcException;
import com.jn.langx.util.Throwables;
import feign.InvocationHandlerFactory;
import feign.MethodMetadata;
import feign.codec.Decoder;

import java.lang.reflect.Type;

/**
 * @since 2.6.0
 */
public class UnifiedResponseRestErrorHandler implements ErrorHandler<Throwable> {

    private Decoder decoder;

    public void setDecoder(Decoder decoder) {
        this.decoder = decoder;
    }

    @Override
    public Object apply(Throwable e, InvocationHandlerFactory.MethodHandler methodHandler) {
        if(e instanceof FeignRpcException) {
            FeignRpcException rpcException = (FeignRpcException)e;
            MethodMetadata metadata = Feigns.getMethodMetadata(methodHandler);
            if (metadata != null) {
                Type expectedType = metadata.returnType();
                try {
                    return decoder.decode(rpcException.originalResponse(), expectedType);
                } catch (Throwable ex) {
                    throw Throwables.wrapAsRuntimeException(ex);
                }
            }
        }
        return null;
    }
}
