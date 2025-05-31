package com.jn.agileway.httpclient.core.payload;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpRequest;
import com.jn.agileway.httpclient.core.error.exception.BadHttpRequestException;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.util.collection.multivalue.MultiValueMap;
import com.jn.langx.util.net.mime.MediaType;
import com.jn.langx.util.net.uri.component.UriComponentUtils;
import com.jn.langx.util.reflect.Reflects;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class GeneralFormHttpRequestWriter implements HttpRequestPayloadWriter {
    public boolean canWrite(HttpRequest<?> request) {
        MediaType contentType = request.getHttpHeaders().getContentType();
        if (!HttpClientUtils.isSimpleForm(contentType)) {
            return false;
        }
        return true;
    }

    public void write(HttpRequest<?> request, ByteArrayOutputStream output) throws Exception {
        Object body = request.getPayload();
        MediaType contentType = request.getHttpHeaders().getContentType();
        Charset charset = contentType.getCharset();
        String formString = serializeSimpleForm(request, body, contentType.getCharset(), output);
        output.write(formString.getBytes(charset));
    }

    private String serializeSimpleForm(HttpRequest<?> request, Object formData, final Charset charset, ByteArrayOutputStream output) throws UnsupportedEncodingException {
        if (formData instanceof String) {
            return (String) formData;
        }
        if (formData instanceof StringBuilder) {
            return formData.toString();
        }
        if (formData instanceof Map) {
            return serializeMap((Map) formData, charset);
        }
        if (formData instanceof MultiValueMap) {
            return serializeMultiValueMap((MultiValueMap) formData, charset);
        }
        throw new BadHttpRequestException(request.getMethod(), request.getUri(), "the form data type is not supported, the type is " + Reflects.getFQNClassName(formData.getClass()));
    }

    private String serializeMap(Map<String, ?> formData, final Charset charset) throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();
        Set<String> names = formData.keySet();
        for (String name : names) {
            String encodedName = URLEncoder.encode(name, charset.name());
            if (builder.length() != 0) {
                builder.append('&');
            }
            builder.append(encodedName).append('=');
            Object value = formData.get(name);
            String stringValue = UriComponentUtils.getQueryParamValue(value);
            if (stringValue != null) {
                builder.append(URLEncoder.encode(stringValue, charset.name()));
            }
        }
        return builder.toString();
    }

    private String serializeMultiValueMap(MultiValueMap<String, ?> formData, final Charset charset) throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();
        Set<String> names = formData.keySet();
        for (String name : names) {
            String encodedName = URLEncoder.encode(name, charset.name());
            Collection values = formData.get(name);
            for (Object value : values) {
                if (builder.length() != 0) {
                    builder.append('&');
                }
                builder.append(encodedName).append('=');
                String stringValue = UriComponentUtils.getQueryParamValue(value);
                if (stringValue != null) {
                    builder.append(URLEncoder.encode(stringValue, charset.name()));
                }
            }
        }
        return builder.toString();
    }
}
