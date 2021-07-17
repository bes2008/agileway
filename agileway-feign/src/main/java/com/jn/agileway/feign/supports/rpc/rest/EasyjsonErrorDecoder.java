package com.jn.agileway.feign.supports.rpc.rest;

import com.jn.agileway.feign.Feigns;
import com.jn.agileway.feign.supports.rpc.FeignRPCException;
import com.jn.langx.util.io.IOs;
import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.IOException;

public class EasyjsonErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response){
        FeignRPCException exception = new FeignRPCException(methodKey, response);
        try {
            Response repeatableResponse = Feigns.toByteArrayResponse(response);
            IOs.close(response);
            response = repeatableResponse;
            exception.setResponse(repeatableResponse);
        } catch (IOException ex) {
            return new Default().decode(methodKey, response);
        }
        return exception;
    }

}
