package com.jn.agileway.httpclient.core.payload;

import com.jn.agileway.httpclient.core.HttpRequest;

import java.io.OutputStream;

public interface HttpRequestAttachmentPayloadLogging {
    void loggingPayload(HttpRequest<?> request, OutputStream output);
}
