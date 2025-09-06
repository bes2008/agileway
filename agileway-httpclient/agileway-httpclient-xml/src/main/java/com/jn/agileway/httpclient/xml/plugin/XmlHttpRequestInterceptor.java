package com.jn.agileway.httpclient.xml.plugin;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.interceptor.HttpRequestInterceptor;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.net.mime.MediaType;

import java.nio.charset.Charset;

public class XmlHttpRequestInterceptor implements HttpRequestInterceptor {
    private static final MediaType expectedContentType = new MediaType("application", "xml", Charsets.UTF_8);

    @Override
    public void intercept(HttpRequest request) {
        MediaType contentType = request.getHttpHeaders().getContentType();
        if (contentType == null) {
            return;
        }
        if (!expectedContentType.equalsTypeAndSubtype(contentType)) {
            return;
        }
        Charset charset = contentType.getCharset();
        if (charset == null || !charset.equals(Charsets.UTF_8)) {
            request.getHttpHeaders().setContentType(expectedContentType);
        }
    }
}
