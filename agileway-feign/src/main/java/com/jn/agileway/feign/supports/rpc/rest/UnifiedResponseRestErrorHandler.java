package com.jn.agileway.feign.supports.rpc.rest;

import com.jn.agileway.feign.ErrorHandler;
import com.jn.agileway.feign.Feigns;
import com.jn.agileway.feign.supports.rpc.ClientWrapper;
import com.jn.agileway.feign.supports.rpc.FeignRR;
import com.jn.agileway.feign.supports.rpc.FeignRpcException;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.logging.Loggers;
import feign.FeignException;
import feign.InvocationHandlerFactory;
import feign.MethodMetadata;
import feign.codec.Decoder;
import org.slf4j.Logger;

import java.lang.reflect.Type;

/**
 * @since 2.6.0
 */
public class UnifiedResponseRestErrorHandler implements ErrorHandler<Throwable> {
    private static final Logger logger = Loggers.getLogger(UnifiedResponseRestErrorHandler.class);
    private Decoder decoder;

    public void setDecoder(Decoder decoder) {
        this.decoder = decoder;
    }

    @Override
    public Decoder getDecoder() {
        return decoder;
    }

    @Override
    public Object apply(Throwable e, InvocationHandlerFactory.MethodHandler methodHandler) {
        if (e instanceof FeignRpcException) {
            return handleFeignRpcException((FeignRpcException) e, methodHandler);
        } else if (e instanceof FeignException) {
            return handleFeignException((FeignException) e, methodHandler);
        } else {
            return handleThrowable(e, methodHandler);
        }
    }

    /**
     * @since 2.7.8
     */
    protected Object handleFeignRpcException(FeignRpcException e, InvocationHandlerFactory.MethodHandler methodHandler) {
        MethodMetadata metadata = Feigns.getMethodMetadata(methodHandler);
        if (metadata != null) {
            Type expectedType = metadata.returnType();
            try {
                return decoder.decode(e.originalResponse(), expectedType);
            } catch (Throwable ex) {
                throw Throwables.wrapAsRuntimeException(ex);
            }
        }
        return null;
    }

    /**
     * @since 2.7.8
     */
    protected Object handleFeignException(FeignException e, InvocationHandlerFactory.MethodHandler methodHandler) {
        FeignRR feignRR = ClientWrapper.feignRRHolder.get();
        if (feignRR.getResponse() == null) {
            logger.error(e.getMessage(), e);
            return null;
        } else {
            MethodMetadata metadata = Feigns.getMethodMetadata(methodHandler);
            String methodKey = metadata.configKey();
            FeignRpcException exception = new FeignRpcException(methodKey, feignRR.getResponse(), e);
            return handleFeignRpcException(exception, methodHandler);
        }
    }


    /**
     * @since 2.7.8
     */
    protected Object handleThrowable(Throwable e, InvocationHandlerFactory.MethodHandler methodHandler) {
        logger.error(e.getMessage(), e);
        return null;
    }

}
