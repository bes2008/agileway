package com.jn.agileway.httpclient.jetty;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.MessageHeaderConstants;
import com.jn.agileway.httpclient.core.payload.HttpRequestPayloadWriter;
import com.jn.agileway.httpclient.core.underlying.AbstractUnderlyingHttpExecutor;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.agileway.httpclient.util.ContentEncoding;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.struct.Holder;
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

    JettyUnderlyingHttpExecutor(HttpClient httpClient, TimeDuration timeout) {
        this.httpClient = httpClient;
        this.timeout = timeout;
    }

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
    public UnderlyingHttpResponse executeAttachmentUploadRequest(HttpRequest<?> request, final HttpRequestPayloadWriter payloadWriter) throws Exception {
        HttpMethod method = request.getMethod();
        URI uri = request.getUri();

        Request jettyHttpRequest = this.httpClient.newRequest(uri);
        jettyHttpRequest.method(method.name());

        completeHeaders(request, jettyHttpRequest);
        Holder<OutputStream> outHolder = new Holder<OutputStream>();
        if (HttpClientUtils.isSupportContentMethod(method) && request.getPayload() != null) {
            List<ContentEncoding> contentEncodings = HttpClientUtils.getContentEncodings(request.getHttpHeaders());
            OutputStreamRequestContent content = new OutputStreamRequestContent(request.getHttpHeaders().getContentType().toString());
            OutputStream out = content.getOutputStream();
            if (!Objs.isEmpty(contentEncodings)) {
                out = HttpClientUtils.wrapByContentEncodings(out, contentEncodings);
            }
            jettyHttpRequest.body(content);
            outHolder.set(out);
        }
        TimeDuration requestTimeout = request.getHeaders().get(MessageHeaderConstants.REQUEST_KEY_TIMEOUT, TimeDuration.class);
        if (requestTimeout == null) {
            requestTimeout = this.timeout;
        }
        if (requestTimeout != null) {
            requestTimeout = TimeDuration.ofMinutes(2);
        }
        jettyHttpRequest.timeout(requestTimeout.toMillis(), TimeUnit.MILLISECONDS);
        InputStreamResponseListener responseListener = new InputStreamResponseListener();
        final Holder<Result> resultHolder = new Holder<Result>();
        jettyHttpRequest.send(responseListener);

        Exception ex = null;
        if (!outHolder.isEmpty()) {
            try {
                payloadWriter.write(request, outHolder.get());
            } catch (Exception e) {
                ex = e;
                Loggers.getLogger(JettyUnderlyingHttpExecutor.class).error("file upload failed: {}", e.getMessage(), e);
            } finally {
                IOs.close(outHolder.get());
            }
        }

        Response response = responseListener.get(requestTimeout.toMillis(), TimeUnit.MILLISECONDS);

        int statusCode = response.getStatus();
        HttpHeaders responseHeaders = new HttpHeaders();
        Pipeline.of(response.getHeaders())
                .forEach(new Consumer<HttpField>() {
                    @Override
                    public void accept(HttpField header) {
                        responseHeaders.add(header.getName(), header.getValue());
                    }
                });
        InputStream responseContentInputStream = responseListener.getInputStream();
        return new JettyUnderlyingHttpResponse(method, uri, responseHeaders, statusCode, responseContentInputStream);

    }
}
