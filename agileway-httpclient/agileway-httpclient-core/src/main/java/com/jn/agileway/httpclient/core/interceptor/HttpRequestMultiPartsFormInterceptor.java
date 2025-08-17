package com.jn.agileway.httpclient.core.interceptor;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.MessageHeaderConstants;
import com.jn.agileway.httpclient.core.error.exception.BadHttpRequestException;
import com.jn.agileway.httpclient.core.payload.multipart.FormPartAdapter;
import com.jn.agileway.httpclient.core.payload.multipart.MultiPartsForm;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.collection.multivalue.MultiValueMap;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.net.mime.MediaType;
import com.jn.langx.util.spi.CommonServiceProvider;

import java.nio.charset.Charset;
import java.util.Map;

public class HttpRequestMultiPartsFormInterceptor implements HttpRequestInterceptor {
    private FormPartAdapter[] adapters;

    public HttpRequestMultiPartsFormInterceptor() {
        this.adapters = Pipeline.of(CommonServiceProvider.<FormPartAdapter>loadService(FormPartAdapter.class))
                .toArray(FormPartAdapter[].class);
    }

    @Override
    public void intercept(HttpRequest request) {
        if (request.getPayload() instanceof MultiPartsForm) {
            if (request.getMethod() != HttpMethod.POST) {
                throw new BadHttpRequestException(request.getMethod(), request.getUri(), StringTemplates.formatWithPlaceholder("multipart/form-data content should use the POST, current is: {}", request.getMethod()));
            }
            MediaType contentType = request.getHttpHeaders().getContentType();
            if (contentType != null) {
                if (!MediaType.MULTIPART_FORM_DATA.equalsTypeAndSubtype(contentType)) {
                    throw new BadHttpRequestException(request.getMethod(), request.getUri(), StringTemplates.formatWithPlaceholder("multipart/form-data content should use the multipart/form-data content-type, current is: {}", contentType));
                }
            } else {
                request.getHttpHeaders().setContentType(MediaType.MULTIPART_FORM_DATA);
            }
        }
        MediaType contentType = request.getHttpHeaders().getContentType();
        if (!MediaType.MULTIPART_FORM_DATA.equalsTypeAndSubtype(contentType)) {
            return;
        }
        if (request.getMethod() != HttpMethod.POST) {
            throw new BadHttpRequestException(request.getMethod(), request.getUri(), StringTemplates.formatWithPlaceholder("multipart/form-data content should use the POST, current is: {}", request.getMethod()));
        }
        if (!(request.getPayload() instanceof MultiPartsForm)) {
            try {
                if (request.getPayload() instanceof MultiValueMap) {
                    request.setPayload(MultiPartsForm.ofMultiValueMap((MultiValueMap) request.getPayload(), adapters));
                } else if (request.getPayload() instanceof Map) {
                    request.setPayload(MultiPartsForm.ofMap((Map) request.getPayload(), adapters));
                } else {
                    throw new BadHttpRequestException(request.getMethod(), request.getUri(), StringTemplates.formatWithPlaceholder("invalid multipart/form-data content "));
                }
            } catch (Throwable ex) {
                throw new BadHttpRequestException(request.getMethod(), request.getUri(), StringTemplates.formatWithPlaceholder("invalid multipart/form-data content "));
            }
        }

        String boundary = HttpClientUtils.generateMultipartBoundary();
        MultiPartsForm form = (MultiPartsForm) request.getPayload();
        Charset formCharset = form.getCharset() == null ? Charsets.UTF_8 : form.getCharset();

        contentType = new MediaType(contentType, "boundary", boundary);
        contentType = new MediaType(contentType, "charset", formCharset.name());
        request.getHttpHeaders().setContentType(contentType);

        request.getHeaders().put(MessageHeaderConstants.REQUEST_KEY_IS_ATTACHMENT_UPLOAD, true);
    }
}
