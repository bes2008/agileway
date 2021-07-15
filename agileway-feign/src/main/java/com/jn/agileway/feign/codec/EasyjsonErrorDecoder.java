package com.jn.agileway.feign.codec;

import com.jn.langx.util.io.IOs;
import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.IOException;

public class EasyjsonErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        FeignRestRespBodyException exception = new FeignRestRespBodyException(methodKey, response);
        try {
            String body = IOs.readAsString(response.body().asReader());
            exception.setResponseBody(body);
        } catch (IOException ex) {
            return new Default().decode(methodKey, response);
        }
        return exception;
    }

}
