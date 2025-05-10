package com.jn.agileway.httpclient.core;

import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.io.resource.Resource;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.net.mime.MediaType;

public class HttpRequestHeadersInterceptor implements HttpRequestInterceptor {
    @Override
    public void intercept(HttpRequest request) {
        handleContentTypeAndLength(request);
    }

    private void handleContentTypeAndLength(HttpRequest request) {
        HttpMethod method = request.getMethod();
        MediaType contentType = request.getHeaders().getContentType();
        switch (method) {
            case GET:
            case HEAD:
            case OPTIONS:
            case TRACE:
                request.getHeaders().setContentType(null);
                request.getHeaders().setContentLength(0L);
                break;
            case DELETE:
            case PATCH:
            case PUT:
                if (contentType == null) {
                    request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                }
                if (request.getBody() == null) {
                    request.getHeaders().setContentLength(0L);
                }
                break;
            case POST:
            default:
                if (request.getBody() == null) {
                    request.getHeaders().setContentLength(0L);
                } else if (request.getBody() instanceof MultiplePartsBody || request.getBody() instanceof Resource) {
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
    }
}
