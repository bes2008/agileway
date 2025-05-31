package com.jn.agileway.httpclient.core.interceptor;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.payload.multipart.MultiPartsForm;
import com.jn.agileway.httpclient.core.error.exception.BadHttpRequestException;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.collection.multivalue.MultiValueMap;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.net.mime.MediaType;

/**
 * 对 Header 进行默认值处理
 */
public class HttpRequestHeadersInterceptor implements HttpRequestInterceptor {
    private MultiValueMap<String, String> fixedHeaders;

    public HttpRequestHeadersInterceptor(MultiValueMap<String, String> fixedHeaders) {
        this.fixedHeaders = fixedHeaders;
    }

    @Override
    public void intercept(HttpRequest request) {
        addFixedHeaders(request);
        handleContentType(request);
    }

    private void addFixedHeaders(HttpRequest request) {
        if (fixedHeaders != null) {
            request.getHttpHeaders().addAll(fixedHeaders);
        }
    }

    private void handleContentType(HttpRequest request) {
        HttpMethod method = request.getMethod();
        MediaType contentType = request.getHttpHeaders().getContentType();

        // 确保要上传body的请求，都设置了 Content-Type
        switch (method) {
            case GET:
            case HEAD:
            case OPTIONS:
            case DELETE:
            case TRACE:
                request.getHttpHeaders().remove("Content-Type");
                break;
            case PATCH:
            case PUT:
            case POST:
            default:
                if (request.getPayload() != null && (request.getPayload() instanceof MultiPartsForm)) {
                    request.getHttpHeaders().setContentType(MediaType.MULTIPART_FORM_DATA);
                    contentType = request.getHttpHeaders().getContentType();
                }
                if (contentType == null) {
                    request.getHttpHeaders().setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                }
                contentType = request.getHttpHeaders().getContentType();
                if (contentType.equalsTypeAndSubtype(MediaType.APPLICATION_FORM_URLENCODED) ||
                        contentType.equalsTypeAndSubtype(MediaType.TEXT_PLAIN) ||
                        contentType.equalsTypeAndSubtype(MediaType.TEXT_HTML)) {
                    request.getHttpHeaders().setContentType(new MediaType(contentType, Charsets.UTF_8));
                    break;
                }
        }

        if (HttpClientUtils.isForm(request.getHttpHeaders().getContentType())) {
            if (!HttpClientUtils.isWriteableMethod(request.getMethod())) {
                throw new BadHttpRequestException(request.getMethod(), request.getUri(), StringTemplates.formatWithPlaceholder("Http request with Content-Type {}, method {} is invalid", request.getHttpHeaders().getContentType(), request.getMethod()));
            }
        }

        contentType = request.getHttpHeaders().getContentType();
        if (contentType != null) {
            if (contentType.getCharset() == null) {
                request.getHttpHeaders().setContentType(new MediaType(contentType, Charsets.UTF_8));
            }
        }

        // Content-Length 由底层 http库完成
        request.getHttpHeaders().remove("Content-Length");
    }
}
