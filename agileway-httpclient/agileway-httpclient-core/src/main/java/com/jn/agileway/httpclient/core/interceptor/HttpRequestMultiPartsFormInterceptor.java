package com.jn.agileway.httpclient.core.interceptor;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.error.exception.BadHttpRequestException;
import com.jn.agileway.httpclient.core.content.multipart.MultiPartsForm;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.collection.multivalue.MultiValueMap;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.net.mime.MediaType;

import java.nio.charset.Charset;
import java.util.Map;

public class HttpRequestMultiPartsFormInterceptor implements HttpRequestInterceptor {
    @Override
    public void intercept(HttpRequest request) {
        if (request.getPayload() instanceof MultiPartsForm) {
            if (request.getMethod() != HttpMethod.POST) {
                throw new BadHttpRequestException(request.getMethod(), request.getUri(), StringTemplates.formatWithPlaceholder("multipart/form-data content should use the POST, current is: {}", request.getMethod()));
            }
            MediaType contentType = request.getHeaders().getContentType();
            if (contentType != null) {
                if (!MediaType.MULTIPART_FORM_DATA.equalsTypeAndSubtype(contentType)) {
                    throw new BadHttpRequestException(request.getMethod(), request.getUri(), StringTemplates.formatWithPlaceholder("multipart/form-data content should use the multipart/form-data content-type, current is: {}", contentType));
                }
            } else {
                request.getHeaders().setContentType(MediaType.MULTIPART_FORM_DATA);
            }
        }

        if (MediaType.MULTIPART_FORM_DATA.equalsTypeAndSubtype(request.getHeaders().getContentType())) {
            if (request.getMethod() != HttpMethod.POST) {
                throw new BadHttpRequestException(request.getMethod(), request.getUri(), StringTemplates.formatWithPlaceholder("multipart/form-data content should use the POST, current is: {}", request.getMethod()));
            }
            if (!(request.getPayload() instanceof MultiPartsForm)) {
                try {
                    if (request.getPayload() instanceof MultiValueMap) {
                        request.setContent(MultiPartsForm.ofMultiValueMap((MultiValueMap) request.getPayload()));
                    } else if (request.getPayload() instanceof Map) {
                        request.setContent(MultiPartsForm.ofMap((Map) request.getPayload()));
                    }
                } catch (Throwable ex) {
                    throw new BadHttpRequestException(request.getMethod(), request.getUri(), StringTemplates.formatWithPlaceholder("invalid multipart/form-data content "));
                }
            }
        }

        if (request.getPayload() instanceof MultiPartsForm) {
            MediaType contentType = request.getHeaders().getContentType();
            String boundary = HttpClientUtils.generateMultipartBoundary();
            MultiPartsForm form = (MultiPartsForm) request.getPayload();
            Charset formCharset = form.getCharset() == null ? Charsets.UTF_8 : form.getCharset();

            contentType = new MediaType(contentType, "boundary", boundary);
            contentType = new MediaType(contentType, "charset", formCharset.name());
            request.getHeaders().setContentType(contentType);
        }
    }
}
