package com.jn.agileway.httpclient.core.interceptor;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.HttpRequestInterceptor;
import com.jn.agileway.httpclient.core.multipart.MultipartsForm;
import com.jn.agileway.httpclient.core.exception.BadHttpRequestException;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.io.resource.Resource;
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
        handleContentTypeAndLength(request);
    }

    private void addFixedHeaders(HttpRequest request) {
        if (fixedHeaders != null) {
            request.getHeaders().addAll(fixedHeaders);
        }
    }

    private void handleContentTypeAndLength(HttpRequest request) {
        HttpMethod method = request.getMethod();
        MediaType contentType = request.getHeaders().getContentType();

        // 确保要上传body的请求，都设置了 Content-Type
        switch (method) {
            case GET:
            case HEAD:
            case OPTIONS:
            case DELETE:
            case TRACE:
                request.getHeaders().setContentType(null);
                request.getHeaders().setContentLength(0L);
                break;
            case PATCH:
            case PUT:
                if (contentType == null) {
                    request.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
                }
                if (request.getContent() == null) {
                    request.getHeaders().setContentLength(0L);
                }
                break;
            case POST:
            default:
                if (request.getContent() == null) {
                    request.getHeaders().setContentLength(0L);
                } else if (request.getContent() instanceof MultipartsForm || request.getContent() instanceof Resource) {
                    request.getHeaders().setContentType(MediaType.MULTIPART_FORM_DATA);
                    contentType = request.getHeaders().getContentType();
                    // 文件上传时，不设置 Content-Length,要改用 chunked
                }
                if (contentType == null) {
                    request.getHeaders().setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                }
                break;
        }

        if (HttpClientUtils.requestBodyUseStreamMode(method, request.getHeaders())) {
            request.getHeaders().remove("Content-Length");
        }

        if (HttpClientUtils.isForm(request.getHeaders().getContentType())) {
            if (request.getMethod() != HttpMethod.POST) {
                throw new BadHttpRequestException(StringTemplates.formatWithPlaceholder("Http request with Content-Type {}, method {} is invalid", request.getHeaders().getContentType(), request.getMethod()));
            }
        }

        contentType = request.getHeaders().getContentType();
        if (contentType != null) {
            if (contentType.getCharset() == null) {
                request.getHeaders().setContentType(new MediaType(contentType, Charsets.UTF_8));
            }
        }
    }
}
