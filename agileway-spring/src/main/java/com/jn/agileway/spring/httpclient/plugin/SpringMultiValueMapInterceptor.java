package com.jn.agileway.spring.httpclient.plugin;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.interceptor.HttpRequestInterceptor;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.agileway.spring.httpclient.adapter.AdaptedMultipleValueMap;
import com.jn.langx.util.net.mime.MediaType;
import org.springframework.util.MultiValueMap;

public class SpringMultiValueMapInterceptor implements HttpRequestInterceptor {
    public void intercept(HttpRequest request) {
        MediaType contentType = request.getHttpHeaders().getContentType();
        if (HttpClientUtils.isMultipartForm(contentType)) {
            if (request.getPayload() instanceof MultiValueMap) {
                MultiValueMap mvmap = (MultiValueMap) request.getPayload();
                request.setPayload(new AdaptedMultipleValueMap<>(mvmap));
            }
        }
    }
}
