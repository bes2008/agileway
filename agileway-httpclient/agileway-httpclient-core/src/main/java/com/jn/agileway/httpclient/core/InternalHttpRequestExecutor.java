package com.jn.agileway.httpclient.core;

import com.jn.agileway.eipchannel.core.endpoint.exchange.RequestReplyExecutor;
import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.agileway.httpclient.core.error.exception.HttpTimeoutException;
import com.jn.agileway.httpclient.core.payload.HttpRequestPayloadWriter;
import com.jn.agileway.httpclient.core.payload.HttpResponsePayloadExtractor;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpExecutor;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.retry.RetryConfig;
import com.jn.langx.util.retry.Retryer;

import java.lang.reflect.Type;
import java.util.concurrent.Callable;

final class InternalHttpRequestExecutor extends RequestReplyExecutor {
    private UnderlyingHttpExecutor underlyingHttpExecutor;


    void setUnderlyingHttpExecutor(UnderlyingHttpExecutor underlyingHttpExecutor) {
        this.underlyingHttpExecutor = underlyingHttpExecutor;
    }

    @Override
    public boolean sink(Message<?> message) {
        HttpRequest<?> request = (HttpRequest<?>) message;
        Retryer<Boolean> retryer = (Retryer<Boolean>) request.getHeaders().get(MessageHeaderConstants.REQUEST_KEY_RETRY);
        retryer = retryer == null ? new Retryer(RetryConfig.noneRetryConfig()) : retryer;

        // 返回请求是否被 执行了
        boolean executed = retryer.execute(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                UnderlyingHttpResponse response;
                if (Boolean.TRUE.equals(request.getHeaders().get(MessageHeaderConstants.REQUEST_KEY_IS_ATTACHMENT_UPLOAD))) {
                    HttpRequestPayloadWriter payloadWriter = (HttpRequestPayloadWriter) request.getHeaders().get(MessageHeaderConstants.REQUEST_KEY_ATTACHMENT_UPLOAD_WRITER);
                    response = underlyingHttpExecutor.executeAttachmentUploadRequest(request, payloadWriter);
                } else {
                    response = underlyingHttpExecutor.executeBufferedRequest(request);
                }
                request.getHeaders().put(MessageHeaderConstants.REQUEST_KEY_UNDERLYING_RESPONSE, response);
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
        waitHttpResponse(request, timeoutInMills);
        return extractPayload(request);
    }

    private void waitHttpResponse(HttpRequest request, long timeoutInMills) {
        UnderlyingHttpResponse underlyingHttpResponse = null;
        long start = System.currentTimeMillis();
        while (underlyingHttpResponse == null) {
            underlyingHttpResponse = (UnderlyingHttpResponse) request.getHeaders().get(MessageHeaderConstants.REQUEST_KEY_UNDERLYING_RESPONSE);
            if (underlyingHttpResponse == null) {
                long now = System.currentTimeMillis();
                if (now - start > timeoutInMills) {
                    throw new HttpTimeoutException(request.getMethod(), request.getUri(), "http response timeout");
                }
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }
    }

    private HttpResponse extractPayload(HttpRequest request) {
        UnderlyingHttpResponse underlyingHttpResponse = (UnderlyingHttpResponse) request.getHeaders().get(MessageHeaderConstants.REQUEST_KEY_UNDERLYING_RESPONSE);
        Type expectedResponseType = (Type) request.getHeaders().get(MessageHeaderConstants.RESPONSE_KEY_REPLY_PAYLOAD_TYPE);
        HttpResponsePayloadExtractor contentExtractor = (HttpResponsePayloadExtractor) request.getHeaders().get(MessageHeaderConstants.REQUEST_KEY_REPLY_PAYLOAD_EXTRACTOR);
        HttpResponsePayloadExtractor errorContentExtractor = (HttpResponsePayloadExtractor) request.getHeaders().get(MessageHeaderConstants.REQUEST_KEY_REPLY_PAYLOAD_ERROR_EXTRACTOR);


        clearRequestMessage();
        HttpResponse response = null;
        try {
            boolean needReadBody = needReadBody(underlyingHttpResponse);

            if (needReadBody) {
                if (underlyingHttpResponse.getStatusCode() >= 400) {
                    if (errorContentExtractor != null) {
                        response = errorContentExtractor.extract(underlyingHttpResponse);
                    }
                    if (response == null) {
                        String errorMessage = IOs.readAsString(underlyingHttpResponse.getPayload());
                        response = new HttpResponse<>(underlyingHttpResponse.getMethod(),
                                underlyingHttpResponse.getUri(),
                                underlyingHttpResponse.getStatusCode(),
                                underlyingHttpResponse.getHeaders(),
                                errorMessage, null);
                    }
                } else {
                    if (contentExtractor != null) {
                        response = contentExtractor.extract(underlyingHttpResponse);
                    } else {
                        byte[] bytes = IOs.toByteArray(underlyingHttpResponse.getPayload());
                        response = new HttpResponse<>(underlyingHttpResponse.getMethod(),
                                underlyingHttpResponse.getUri(),
                                underlyingHttpResponse.getStatusCode(),
                                underlyingHttpResponse.getHeaders(),
                                null, bytes);
                    }
                }
            } else {
                if (underlyingHttpResponse.getPayload() != null) {
                    // 消耗掉
                    IOs.toByteArray(underlyingHttpResponse.getPayload());
                }
                response = new HttpResponse<>(underlyingHttpResponse.getMethod(),
                        underlyingHttpResponse.getUri(),
                        underlyingHttpResponse.getStatusCode(),
                        underlyingHttpResponse.getHeaders(),
                        null, null);
            }
        } catch (Exception ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        } finally {
            underlyingHttpResponse.close();
        }

        response.getHeaders().put(MessageHeaderConstants.RESPONSE_KEY_REPLY_PAYLOAD_TYPE, expectedResponseType);

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
