package com.jn.agileway.feign.supports.adaptable;

import feign.Response;
import feign.codec.ErrorDecoder;

public class AdaptableErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        return null;
    }
}
