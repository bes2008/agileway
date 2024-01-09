package com.jn.agileway.feign;

import feign.FeignException;

public class AgilewayFeignDecodeException extends FeignException {
    public AgilewayFeignDecodeException(int status, String message) {
        super( status, message);
    }
}
