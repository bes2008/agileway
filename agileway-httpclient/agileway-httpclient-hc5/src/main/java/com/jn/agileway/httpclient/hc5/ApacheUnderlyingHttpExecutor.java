package com.jn.agileway.httpclient.hc5;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.payload.HttpRequestPayloadWriter;
import com.jn.agileway.httpclient.core.underlying.AbstractUnderlyingHttpExecutor;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;

public class ApacheUnderlyingHttpExecutor extends AbstractUnderlyingHttpExecutor {
    private CloseableHttpAsyncClient httpClient;
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
