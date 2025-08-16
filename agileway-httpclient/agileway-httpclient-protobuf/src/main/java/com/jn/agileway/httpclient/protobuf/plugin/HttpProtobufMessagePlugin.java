package com.jn.agileway.httpclient.protobuf.plugin;

import com.google.protobuf.GeneratedMessage;
import com.jn.agileway.httpclient.core.HttpMessage;
import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.plugin.HttpMessageProtocolPlugin;
import com.jn.langx.util.net.mime.MediaType;

public class HttpProtobufMessagePlugin extends HttpMessageProtocolPlugin {

    @Override
    protected void initInternal() {
        this.requestInterceptors.add(new ProtobufHttpRequestInterceptor());
        this.requestPayloadWriters.add(new ProtobufMessageWriter());
        this.responsePayloadReaders.add(new ProtobufMessageReader());
    }

    @Override
    public boolean availableFor(HttpMessage httpMessage) {
        Object payload = httpMessage.getPayload();
        if (payload == null) {
            return false;
        }
        if (httpMessage instanceof HttpRequest<?>) {
            if (!(payload instanceof GeneratedMessage)) {
                return false;
            }
        }

        MediaType contentType = httpMessage.getHttpHeaders().getContentType();
        if (contentType != null) {
            if (MediaType.APPLICATION_JSON.equalsTypeAndSubtype(contentType) || MediaType.APPLICATION_PROTOBUF.equalsTypeAndSubtype(contentType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getName() {
        return "protobuf";
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE - 100;
    }

    @Override
    public void destroy() {

    }
}
