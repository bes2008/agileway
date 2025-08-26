package com.jn.agileway.httpclient.protobuf.plugin;

import com.google.protobuf.GeneratedMessage;
import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.interceptor.HttpRequestInterceptor;
import com.jn.langx.util.net.mime.MediaType;

public class ProtobufHttpRequestInterceptor implements HttpRequestInterceptor {
    @Override
    public void intercept(HttpRequest request) {
        Object payload = request.getPayload();
        if (payload == null) {
            return;
        }

        MediaType contentType = request.getHttpHeaders().getContentType();
        if (contentType == null) {
            if (payload instanceof GeneratedMessage) {
                contentType = MediaType.APPLICATION_PROTOBUF;

                request.getHttpHeaders().setContentType(contentType);
            }
        }

    }
}
