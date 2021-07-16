package com.jn.agileway.feign.supports.adaptable;

import com.jn.agileway.feign.Feigns;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.Preconditions;
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
public class AdaptableDecoder implements Decoder {
    private Decoder delegate;
    private ResponseBodyAdapter adapter;

    public AdaptableDecoder(@NonNull Decoder delegate, ResponseBodyAdapter adapter) {
        this.delegate = delegate;
        setAdapter(adapter);
    }

    public void setAdapter(ResponseBodyAdapter adapter) {
        Preconditions.checkNotNull(adapter);
        this.adapter = adapter;
    }

    @Override
    public Object decode(Response response, Type expected) throws IOException, DecodeException, FeignException {
        if (response == null || response.body() == null) {
            return null;
        }
        if (expected == Response.class) {
            return response;
        }

        Response.Body body = response.body();
        Response repeatableResponse = response;
        if (!body.isRepeatable()) {
            repeatableResponse = Feigns.toByteArrayResponse(repeatableResponse);
        }

        Object result = delegate.decode(repeatableResponse, expected);
        result = adapter.adapt(repeatableResponse, expected, result);

        if (repeatableResponse != response) {
            IOs.close(response);
        }
        return result;
    }

}
