package com.jn.agileway.feign.supports.rpc.rest;

import com.jn.agileway.feign.Feigns;
import com.jn.agileway.feign.supports.rpc.FeignRpcException;
import com.jn.langx.util.io.IOs;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class EasyjsonErrorDecoder implements ErrorDecoder {
    private static final Logger logger = LoggerFactory.getLogger(EasyjsonErrorDecoder.class);

    @Override
    public Exception decode(String methodKey, Response response) {
        FeignRpcException exception = new FeignRpcException(methodKey, response);
        try {
            Response repeatableResponse = Feigns.toByteArrayResponse(response);
            IOs.close(response);
            response = repeatableResponse;
            exception.setResponse(repeatableResponse);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return exception;
    }

}
