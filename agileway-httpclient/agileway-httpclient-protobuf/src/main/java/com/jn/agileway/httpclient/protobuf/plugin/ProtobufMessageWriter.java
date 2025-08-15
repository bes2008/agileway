package com.jn.agileway.httpclient.protobuf.plugin;

import com.google.protobuf.GeneratedMessage;
import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.payload.HttpRequestPayloadWriter;
import com.jn.langx.util.net.mime.MediaType;

import java.io.OutputStream;

public class ProtobufMessageWriter implements HttpRequestPayloadWriter {
    @Override
    public boolean canWrite(HttpRequest<?> request) {
        if (!(request.getPayload() instanceof GeneratedMessage)) {
            return false;
        }
        MediaType contentType = request.getHttpHeaders().getContentType();
        if (contentType == null) {
            return false;
        }
        if (!contentType.equalsTypeAndSubtype(MediaType.APPLICATION_PROTOBUF) && !contentType.equalsTypeAndSubtype(MediaType.APPLICATION_JSON)) {
            return false;
        }
        return true;
    }

    @Override
    public void write(HttpRequest<?> request, OutputStream output) throws Exception {

    }
}
