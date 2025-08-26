package com.jn.agileway.httpclient.jetty;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.payload.HttpRequestPayloadWriter;
import com.jn.agileway.httpclient.core.underlying.AbstractUnderlyingHttpExecutor;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;

public class JettyUnderlyingHttp2Executor extends AbstractUnderlyingHttpExecutor {
    @Override
    protected void addHeaderToUnderlying(Object o, String headerName, String headerValue) {
        
    }

    @Override
    protected void setHeaderToUnderlying(Object o, String headerName, String headerValue) {

    }

    @Override
    public UnderlyingHttpResponse executeBufferedRequest(HttpRequest request) throws Exception {
        return null;
    }

    @Override
    public UnderlyingHttpResponse executeAttachmentUploadRequest(HttpRequest request, HttpRequestPayloadWriter payloadWriter) throws Exception {
        return null;
    }
}
