package com.jn.agileway.httpclient.protobuf.plugin;

import com.google.protobuf.GeneratedMessage;
import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.payload.HttpRequestPayloadWriter;

import java.io.OutputStream;

public class ProtobufMessageWriter implements HttpRequestPayloadWriter {
    @Override
    public boolean canWrite(HttpRequest<?> request) {
        if (request.getPayload() instanceof GeneratedMessage) {
            return true;
        }
        return false;
    }

    @Override
    public void write(HttpRequest<?> request, OutputStream output) throws Exception {

    }
}
