package com.jn.agileway.httpclient.core.interceptor;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.HttpRequestInterceptor;
import com.jn.agileway.httpclient.core.exception.BadHttpRequestException;
import com.jn.agileway.httpclient.core.multipart.MultiPartsForm;
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
        if (request.getContent() instanceof MultiPartsForm) {
            if (request.getMethod() != HttpMethod.POST) {
                throw new BadHttpRequestException(StringTemplates.formatWithPlaceholder("multipart/form-data content should use the POST, current is: {}", request.getMethod()));
            }
            MediaType contentType = request.getHeaders().getContentType();
            if (contentType != null) {
                if (!MediaType.MULTIPART_FORM_DATA.equalsTypeAndSubtype(contentType)) {
                    throw new BadHttpRequestException(StringTemplates.formatWithPlaceholder("multipart/form-data content should use the multipart/form-data content-type, current is: {}", contentType));
                }
            } else {
                request.getHeaders().setContentType(MediaType.MULTIPART_FORM_DATA);
            }
        }

        if (MediaType.MULTIPART_FORM_DATA.equalsTypeAndSubtype(request.getHeaders().getContentType())) {
            if (request.getMethod() != HttpMethod.POST) {
                throw new BadHttpRequestException(StringTemplates.formatWithPlaceholder("multipart/form-data content should use the POST, current is: {}", request.getMethod()));
            }
            if (!(request.getContent() instanceof MultiPartsForm)) {
                try {
                    if (request.getContent() instanceof MultiValueMap) {
                        request.setContent(MultiPartsForm.ofMultiValueMap((MultiValueMap) request.getContent()));
                    } else if (request.getContent() instanceof Map) {
                        request.setContent(MultiPartsForm.ofMap((Map) request.getContent()));
                    }
                } catch (Throwable ex) {
                    throw new BadHttpRequestException(StringTemplates.formatWithPlaceholder("invalid multipart/form-data content "));
                }
            }
        }

        if (request.getContent() instanceof MultiPartsForm) {
            MediaType contentType = request.getHeaders().getContentType();
            String boundary = HttpClientUtils.generateMultipartBoundary();
            MultiPartsForm form = (MultiPartsForm) request.getContent();
            Charset formCharset = form.getCharset() == null ? Charsets.UTF_8 : form.getCharset();

            contentType = HttpClientUtils.addContentTypeParameter(contentType, "boundary", boundary);
            contentType = HttpClientUtils.addContentTypeParameter(contentType, "charset", formCharset.name());
            request.getHeaders().setContentType(contentType);
        }
    }
}
