package com.jn.agileway.httpclient.core.interceptor;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.HttpRequestInterceptor;
import com.jn.agileway.httpclient.core.multipart.MultiPartsForm;
import com.jn.agileway.httpclient.core.exception.BadHttpRequestException;
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
            request.getHeaders().addAll(fixedHeaders);
        }
    }

    private void handleContentType(HttpRequest request) {
        HttpMethod method = request.getMethod();
        MediaType contentType = request.getHeaders().getContentType();

        // 确保要上传body的请求，都设置了 Content-Type
        switch (method) {
            case GET:
            case HEAD:
            case OPTIONS:
            case DELETE:
            case TRACE:
                request.getHeaders().remove("Content-Type");
                break;
            case PATCH:
            case PUT:
            case POST:
            default:
                if (request.getContent() != null && (request.getContent() instanceof MultiPartsForm)) {
                    request.getHeaders().setContentType(MediaType.MULTIPART_FORM_DATA);
                    contentType = request.getHeaders().getContentType();
                }
                if (contentType == null) {
                    request.getHeaders().setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                }
                contentType = request.getHeaders().getContentType();
                if (contentType.equalsTypeAndSubtype(MediaType.APPLICATION_FORM_URLENCODED) ||
                        contentType.equalsTypeAndSubtype(MediaType.TEXT_PLAIN) ||
                        contentType.equalsTypeAndSubtype(MediaType.TEXT_HTML)) {
                    request.getHeaders().setContentType(new MediaType(contentType, Charsets.UTF_8));
                    break;
                }
        }

        if (HttpClientUtils.isForm(request.getHeaders().getContentType())) {
            if (!HttpClientUtils.isWriteable(request.getMethod())) {
                throw new BadHttpRequestException(request.getMethod(), request.getUri(), StringTemplates.formatWithPlaceholder("Http request with Content-Type {}, method {} is invalid", request.getHeaders().getContentType(), request.getMethod()));
            }
        }

        contentType = request.getHeaders().getContentType();
        if (contentType != null) {
            if (contentType.getCharset() == null) {
                request.getHeaders().setContentType(new MediaType(contentType, Charsets.UTF_8));
            }
        }

        // Content-Length 由底层 http库完成
        request.getHeaders().remove("Content-Length");
    }
}
