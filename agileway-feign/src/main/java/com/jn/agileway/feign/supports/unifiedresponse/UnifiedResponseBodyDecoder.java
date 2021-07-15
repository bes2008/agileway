package com.jn.agileway.feign.supports.unifiedresponse;

import com.jn.agileway.feign.Feigns;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.io.IOs;
import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 尽最大的可能，将返回值转换为期望的结果
 */
public class UnifiedResponseBodyDecoder implements Decoder {
    private Decoder delegate;

    public UnifiedResponseBodyDecoder(@NonNull Decoder delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
        if (response == null || response.body() == null) {
            return null;
        }
        if (type == Response.class) {
            return response;
        }

        Response.Body body = response.body();
        Response response1 = response;
        if (!body.isRepeatable()) {
            response1 = Feigns.toByteArrayResponse(response1);
        }

        Object result = delegate.decode(response1, type);
        if (response1 != response) {
            IOs.close(response);
        }
        return result;
    }

}
