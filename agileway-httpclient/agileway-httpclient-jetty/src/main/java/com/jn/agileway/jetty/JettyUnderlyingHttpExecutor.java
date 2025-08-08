package com.jn.agileway.jetty;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.MessageHeaderConstants;
import com.jn.agileway.httpclient.core.payload.HttpRequestPayloadWriter;
import com.jn.agileway.httpclient.core.underlying.AbstractUnderlyingHttpExecutor;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.agileway.httpclient.util.ContentEncoding;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.timing.TimeDuration;
import org.eclipse.jetty.client.*;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class JettyUnderlyingHttpExecutor extends AbstractUnderlyingHttpExecutor<Request> {
    private HttpClient httpClient;
    private TimeDuration timeout;

    @Override
    protected void addHeaderToUnderlying(Request request, String headerName, String headerValue) {
        HttpFields.Mutable mutableHeaders = (HttpFields.Mutable) request.getHeaders();
        mutableHeaders.add(headerName, headerValue);
    }

    @Override
    protected void setHeaderToUnderlying(Request request, String headerName, String headerValue) {
        HttpFields.Mutable mutableHeaders = (HttpFields.Mutable) request.getHeaders();
        mutableHeaders.put(headerName, headerValue);
    }

    @Override
    public UnderlyingHttpResponse executeBufferedRequest(HttpRequest<ByteArrayOutputStream> request) throws Exception {
        HttpMethod method = request.getMethod();
        URI uri = request.getUri();

        Request jettyHttpRequest = this.httpClient.newRequest(uri);
        jettyHttpRequest.method(method.name());

        completeHeaders(request, jettyHttpRequest);
        if (HttpClientUtils.isSupportContentMethod(method)) {
            if (request.getPayload() != null) {
                List<ContentEncoding> contentEncodings = HttpClientUtils.getContentEncodings(request.getHttpHeaders());
                if (!Objs.isEmpty(contentEncodings)) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream(request.getPayload().size() / 3);
                    OutputStream out = HttpClientUtils.wrapByContentEncodings(baos, contentEncodings);
                    out.write(request.getPayload().toByteArray());
                    out.flush();
                    jettyHttpRequest.body(new BytesRequestContent(request.getHttpHeaders().getContentType().toString(), baos.toByteArray()));
                    out.close();
                } else {
                    jettyHttpRequest.body(new BytesRequestContent(request.getHttpHeaders().getContentType().toString(), request.getPayload().toByteArray()));
                }
            } else {
                jettyHttpRequest.body(new BytesRequestContent(request.getHttpHeaders().getContentType().toString(), Emptys.EMPTY_BYTES));
            }
        }

        TimeDuration requestTimeout = request.getHeaders().get(MessageHeaderConstants.REQUEST_KEY_TIMEOUT, TimeDuration.class);
        if (requestTimeout == null) {
            requestTimeout = this.timeout;
        }
        if (requestTimeout != null) {
            jettyHttpRequest.timeout(requestTimeout.toMillis(), TimeUnit.MILLISECONDS);
        }

        ContentResponse contentResponse = jettyHttpRequest.send();

        int statusCode = contentResponse.getStatus();
        HttpHeaders responseHeaders = new HttpHeaders();
        Pipeline.of(contentResponse.getHeaders())
                .forEach(new Consumer<HttpField>() {
                    @Override
                    public void accept(HttpField header) {
                        responseHeaders.add(header.getName(), header.getValue());
                    }
                });
        byte[] responseContent = contentResponse.getContent();
        InputStream responseContentInputStream = responseContent == null ? null : new ByteArrayInputStream(responseContent);
        return new JettyUnderlyingHttpResponse(method, uri, responseHeaders, statusCode, responseContentInputStream);
    }

    @Override
    public UnderlyingHttpResponse executeAttachmentUploadRequest(HttpRequest<?> request, HttpRequestPayloadWriter payloadWriter) throws Exception {
        HttpMethod method = request.getMethod();
        URI uri = request.getUri();

        Request jettyHttpRequest = this.httpClient.newRequest(uri);
        jettyHttpRequest.method(method.name());

        completeHeaders(request, jettyHttpRequest);
        if (HttpClientUtils.isSupportContentMethod(method)) {
            if (request.getPayload() != null) {
                List<ContentEncoding> contentEncodings = HttpClientUtils.getContentEncodings(request.getHttpHeaders());


                OutputStreamRequestContent content = new OutputStreamRequestContent(request.getHttpHeaders().getContentType().toString());
                OutputStream out = content.getOutputStream();
                if (!Objs.isEmpty(contentEncodings)) {
                    out = HttpClientUtils.wrapByContentEncodings(out, contentEncodings);
                }
                payloadWriter.write(request, out);
                jettyHttpRequest.body(content);
                content.close();
            } else {
                jettyHttpRequest.body(new BytesRequestContent(request.getHttpHeaders().getContentType().toString(), Emptys.EMPTY_BYTES));
            }
        }
        TimeDuration requestTimeout = request.getHeaders().get(MessageHeaderConstants.REQUEST_KEY_TIMEOUT, TimeDuration.class);
        if (requestTimeout == null) {
            requestTimeout = this.timeout;
        }
        if (requestTimeout != null) {
            jettyHttpRequest.timeout(requestTimeout.toMillis(), TimeUnit.MILLISECONDS);
        }
        ContentResponse contentResponse = jettyHttpRequest.send();

        int statusCode = contentResponse.getStatus();
        HttpHeaders responseHeaders = new HttpHeaders();
        Pipeline.of(contentResponse.getHeaders())
                .forEach(new Consumer<HttpField>() {
                    @Override
                    public void accept(HttpField header) {
                        responseHeaders.add(header.getName(), header.getValue());
                    }
                });
        byte[] responseContent = contentResponse.getContent();
        InputStream responseContentInputStream = responseContent == null ? null : new ByteArrayInputStream(responseContent);
        return new JettyUnderlyingHttpResponse(method, uri, responseHeaders, statusCode, responseContentInputStream);
    }
}
