package com.jn.agileway.httpclient.httpcomponents.httpexchange;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.underlying.AbstractUnderlyingHttpExecutor;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.util.Strings;
import com.jn.langx.util.net.http.HttpMethod;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public class ApacheUnderlyingHttpExecutor extends AbstractUnderlyingHttpExecutor<HttpUriRequest> {
    private CloseableHttpClient httpClient;

    public void setHttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }


    @Override
    protected void addHeaderToUnderlying(HttpUriRequest underlyingRequest, String headerName, String headerValue) {
        // apache httpclient 不支持由用户写 Content-Length，要由要 它自己来写
        if (Strings.equals("Content-Length", headerName, true)) {
            return;
        }
        underlyingRequest.addHeader(headerName, headerValue);
    }

    @Override
    protected void setHeaderToUnderlying(HttpUriRequest underlyingRequest, String headerName, String headerValue) {
        if (Strings.equals("Content-Length", headerName, true)) {
            return;
        }
        underlyingRequest.removeHeaders(headerName);
        underlyingRequest.addHeader(headerName, headerValue);
    }


    @Override
    public UnderlyingHttpResponse execute(HttpRequest<ByteArrayOutputStream> request) throws Exception {
        HttpUriRequest underlyingRequest = createHttpUriRequest(request.getMethod(), request.getUri());
        return exchangeInternal(underlyingRequest, request);
    }


    protected UnderlyingHttpResponse exchangeInternal(HttpUriRequest underlyingRequest, HttpRequest<ByteArrayOutputStream> request) throws IOException {
        writeHeaders(request, underlyingRequest);
        HttpEntity contentEntity = null;
        if (HttpClientUtils.isWriteableMethod(request.getMethod()) && request.getPayload() != null) {
            String contentEncoding = request.getHttpHeaders().getFirst("Content-Encoding");
            if (Strings.isBlank(contentEncoding)) {
                contentEntity = new BufferedHttpEntity(request.getHttpHeaders().getContentType().toString());
            } else {
                contentEntity = new CompressedHttpEntity(request.getHttpHeaders().getContentType().toString(), contentEncoding);
            }

        }

        if (underlyingRequest instanceof HttpEntityEnclosingRequestBase && contentEntity != null) {
            HttpEntityEnclosingRequestBase underlyingRequestBase = (HttpEntityEnclosingRequestBase) underlyingRequest;
            underlyingRequestBase.setEntity(contentEntity);
            request.getPayload().writeTo((OutputStream) contentEntity);
        }
        CloseableHttpResponse underlyingResponse = this.httpClient.execute(underlyingRequest);
        return new ApacheUnderlyingHttpResponse(request.getMethod(), request.getUri(), underlyingResponse);
    }

    private HttpUriRequest createHttpUriRequest(HttpMethod method, URI uri) throws Exception {
        HttpUriRequest request = null;
        switch (method) {
            case GET:
                request = new HttpGet(uri);
                break;
            case POST:
                request = new HttpPost(uri);
                break;
            case PUT:
                request = new HttpPut(uri);
                break;
            case DELETE:
                request = new HttpDelete(uri);
                break;
            case HEAD:
                request = new HttpHead(uri);
                break;
            case OPTIONS:
                request = new HttpOptions(uri);
                break;
            case PATCH:
                request = new HttpPatch(uri);
                break;
            case TRACE:
            default:
                request = new HttpTrace(uri);
                break;
        }

        return request;
    }

}
