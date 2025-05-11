package com.jn.agileway.httpclient.core.serialize;

import com.jn.agileway.httpclient.core.HttpRequestBodyWriter;
import com.jn.agileway.httpclient.core.UnderlyingHttpRequest;
import com.jn.langx.util.collection.multivalue.MultiValueMap;
import com.jn.langx.util.net.mime.MediaType;
import com.jn.langx.util.net.uri.component.UriComponentUtils;
import com.jn.langx.util.reflect.Reflects;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class GeneralFormHttpRequestWriter implements HttpRequestBodyWriter {
    public boolean canWrite(Object body, MediaType contentType) {
        if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(contentType)) {
            return true;
        }
        Class bodyClass = body.getClass();
        if (Reflects.isSubClass(MultiValueMap.class, bodyClass) || Reflects.isSubClass(Map.class, bodyClass)) {
            return true;
        }
        return false;
    }

    public void write(Object body, MediaType contentType, UnderlyingHttpRequest output) throws IOException {

        Charset charset = contentType.getCharset();
        String formString = serializeForm(body, contentType.getCharset());
        output.getBody().write(formString.getBytes(charset));
    }

    private String serializeForm(Object formData, final Charset charset) throws UnsupportedEncodingException {
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
        return formData.toString();
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
