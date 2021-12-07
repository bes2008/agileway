package com.jn.agileway.feign.supports.rpc.rest;

import com.jn.agileway.feign.Feigns;
import com.jn.agileway.feign.supports.rpc.FeignRpcException;
import com.jn.langx.util.logging.Loggers;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;

import java.io.IOException;
/**
 * @since 2.6.0
 */
public class EasyjsonErrorDecoder implements ErrorDecoder {
    private static final Logger logger = Loggers.getLogger(EasyjsonErrorDecoder.class);

    @Override
    public Exception decode(String methodKey, Response response) {
        FeignRpcException exception = new FeignRpcException(methodKey, response);
        try {
            Response repeatableResponse = Feigns.toByteArrayResponse(response);
            exception.setResponse(repeatableResponse);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return exception;
    }

}
