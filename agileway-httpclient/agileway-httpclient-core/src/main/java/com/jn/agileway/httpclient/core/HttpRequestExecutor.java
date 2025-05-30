package com.jn.agileway.httpclient.core;

import com.jn.agileway.eipchannel.core.endpoint.exchange.RequestReplyExecutor;
import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.agileway.httpclient.core.payload.HttpResponsePayloadExtractor;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpRequest;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpRequestFactory;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.retry.RetryConfig;
import com.jn.langx.util.retry.Retryer;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.concurrent.Callable;

final class HttpRequestExecutor extends RequestReplyExecutor {
    private UnderlyingHttpRequestFactory requestFactory;

    private static final String REQUEST_KEY_REPLY = "agileway_http_reply";
    static final String REQUEST_KEY_RETRY = "agileway_http_request_retry";


    /**
     * 请求中，自定义的 正常的 payload extractor
     */
    static final String REQUEST_KEY_REPLY_PAYLOAD_EXTRACTOR = "agileway_http_reply_payload_extractor";
    /**
     * 请求中，自定义的 statusCode >= 400时的 payload extractor
     */
    static final String REQUEST_KEY_REPLY_PAYLOAD_ERROR_EXTRACTOR = "agileway_http_reply_payload_error_extractor";


    void setRequestFactory(UnderlyingHttpRequestFactory requestFactory) {
        this.requestFactory = requestFactory;
    }

    @Override
    public boolean sink(Message<?> message) {
        HttpRequest<?> request = (HttpRequest<?>) message;
        Retryer<Boolean> retryer = (Retryer<Boolean>) request.getHeaders().get(REQUEST_KEY_RETRY);
        retryer = retryer == null ? new Retryer(RetryConfig.noneRetryConfig()) : retryer;

        // 返回请求是否被 执行了
        boolean executed = retryer.execute(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {

                UnderlyingHttpRequest underlyingHttpRequest = requestFactory.create(request.getMethod(), request.getUri(), request.getHttpHeaders());
                if (HttpClientUtils.isWriteable(request.getMethod()) && request.getPayload() != null) {
                    ByteArrayOutputStream payloadOutputStream = (ByteArrayOutputStream) request.getPayload();
                    byte[] payloadBytes = payloadOutputStream.toByteArray();
                    underlyingHttpRequest.getPayload().write(payloadBytes);
                }

                UnderlyingHttpResponse response = underlyingHttpRequest.exchange();
                request.getHeaders().put(REQUEST_KEY_REPLY, response);

                return true;
            }
        });

        return executed;
    }

    @Override
    public Message<?> poll(long timeoutInMills) {
        HttpRequest request = (HttpRequest) getRequestMessage();
        if (request == null) {
            throw new IllegalStateException("can't found the request, invoke setRequestMessage() before poll");
        }

        UnderlyingHttpResponse underlyingHttpResponse = (UnderlyingHttpResponse) request.getHeaders().get(REQUEST_KEY_REPLY);

        Type responseType = (Type) request.getHeaders().get(BaseHttpMessage.HEADER_KEY_REPLY_PAYLOAD_TYPE);
        HttpResponsePayloadExtractor contentExtractor = (HttpResponsePayloadExtractor) request.getHeaders().get(REQUEST_KEY_REPLY_PAYLOAD_EXTRACTOR);
        HttpResponsePayloadExtractor errorContentExtractor = (HttpResponsePayloadExtractor) request.getHeaders().get(REQUEST_KEY_REPLY_PAYLOAD_ERROR_EXTRACTOR);


        clearRequestMessage();
        int statusCode = underlyingHttpResponse.getStatusCode();
        if (statusCode >= 400) {

        }

        underlyingHttpResponse.getPayload();
        boolean needReadBody = needReadBody(underlyingHttpResponse);
        HttpResponse response = null;
        if (needReadBody) {
            if (underlyingHttpResponse.getStatusCode() >= 400) {
                if (errorContentExtractor != null) {
                    response = errorContentExtractor.extract(underlyingHttpResponse);
                }
                if (response == null) {
                    byte[] bytes = IOs.toByteArray(underlyingHttpResponse.getPayload());
                    response = new HttpResponse<>(underlyingHttpResponse, bytes);
                }
            } else {
                if (contentExtractor != null) {
                    response = contentExtractor.extract(underlyingHttpResponse);
                } else {
                    byte[] bytes = IOs.toByteArray(underlyingHttpResponse.getPayload());
                    response = new HttpResponse<>(underlyingHttpResponse, bytes);
                }
            }
        } else {
            if (underlyingHttpResponse.getPayload() != null) {
                // 消耗掉
                IOs.toByteArray(underlyingHttpResponse.getPayload());
            }
            response = new HttpResponse<>(underlyingHttpResponse);
        }

        response.getHeaders().put(BaseHttpMessage.HEADER_KEY_REPLY_PAYLOAD_TYPE, responseType);

        return response;
    }


    private boolean needReadBody(UnderlyingHttpResponse underlyingHttpResponse) {

        if (underlyingHttpResponse.getMethod() == HttpMethod.HEAD) {
            return false;
        }
        if (underlyingHttpResponse.getStatusCode() < 200) {
            return false;
        }
        if (underlyingHttpResponse.getStatusCode() == 204) {
            return false;
        }
        if (underlyingHttpResponse.getStatusCode() == 304) {
            return false;
        }

        if (underlyingHttpResponse.getPayload() == null) {
            return false;
        }

        if (!underlyingHttpResponse.getHttpHeaders().containsKey("Transfer-Encoding")) {
            long contentLength = underlyingHttpResponse.getHttpHeaders().getContentLength();
            if (contentLength == 0L) {
                return false;
            }
        }
        return true;
    }
}
