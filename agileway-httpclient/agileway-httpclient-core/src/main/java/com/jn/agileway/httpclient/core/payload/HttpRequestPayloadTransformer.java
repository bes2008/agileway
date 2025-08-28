package com.jn.agileway.httpclient.core.payload;

import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.agileway.eipchannel.core.transformer.MessageTransformer;
import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.util.HttpClientUtils;

public abstract class HttpRequestPayloadTransformer implements MessageTransformer {

    @Override
    public final Message<?> transform(Message<?> message) {
        HttpRequest request = (HttpRequest) message;
        if (HttpClientUtils.isSupportContentMethod(request.getMethod()) && request.getPayload() != null) {
            return doTransformInternal(request);
        }
        return request;
    }

    protected abstract HttpRequest doTransformInternal(HttpRequest request);
}
