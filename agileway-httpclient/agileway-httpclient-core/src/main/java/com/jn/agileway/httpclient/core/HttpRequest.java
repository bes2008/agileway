package com.jn.agileway.httpclient.core;

import com.jn.agileway.eipchannel.core.message.MessageHeaders;
import com.jn.agileway.httpclient.core.payload.multipart.MultiPartsForm;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.collection.multivalue.MultiValueMap;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.net.mime.MediaType;
import com.jn.langx.util.net.uri.component.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Map;

public class HttpRequest<T> extends BaseHttpMessage<T> {

    HttpRequest(URI uri, HttpMethod method, HttpHeaders headers, T body) {
        this.uri = uri;
        this.method = method;
        this.headers = new MessageHeaders<HttpHeaders>(null, headers == null ? new HttpHeaders() : headers);
        this.payload = body;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public void setPayload(T content) {
        this.payload = content;
    }

    public static HttpRequest create(
            @NonNull HttpMethod method,
            @NonNull String uriTemplate,
            @Nullable Charset uriEncoding,
            @Nullable MultiValueMap<String, Object> queryParams,
            @Nullable Map<String, Object> uriVariables,
            @Nullable HttpHeaders headers,
            @Nullable Object body) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(uriTemplate);
        if (queryParams != null) {
            uriBuilder.queryParams(queryParams);
        }
        if (uriVariables != null) {
            uriBuilder.uriVariables(uriVariables);
        }
        uriEncoding = uriEncoding == null ? Charsets.UTF_8 : uriEncoding;
        URI uri = uriBuilder.enableEncode(uriEncoding).build().toUri();

        return new HttpRequest(uri, method, headers, body);
    }

    public static HttpRequest forGet(
            @NonNull String uriTemplate,
            @Nullable Charset uriEncoding,
            @Nullable MultiValueMap<String, Object> queryParams,
            @Nullable Map<String, Object> uriVariables) {
        return forGet(uriTemplate, uriEncoding, queryParams, uriVariables, null);
    }

    public static HttpRequest forGet(
            @NonNull String uriTemplate,
            @Nullable Charset uriEncoding,
            @Nullable MultiValueMap<String, Object> queryParams,
            @Nullable Map<String, Object> uriVariables,
            @Nullable HttpHeaders headers) {
        return create(HttpMethod.GET, uriTemplate, uriEncoding, queryParams, uriVariables, headers, null);
    }

    public static HttpRequest forDelete(
            @NonNull String uriTemplate,
            @Nullable Charset uriEncoding,
            @Nullable MultiValueMap<String, Object> queryParams,
            @Nullable Map<String, Object> uriVariables) {
        return forDelete(uriTemplate, uriEncoding, queryParams, uriVariables, null);
    }

    public static HttpRequest forDelete(
            @NonNull String uriTemplate,
            @Nullable Charset uriEncoding,
            @Nullable MultiValueMap<String, Object> queryParams,
            @Nullable Map<String, Object> uriVariables,
            @Nullable HttpHeaders headers) {
        return create(HttpMethod.DELETE, uriTemplate, uriEncoding, queryParams, uriVariables, headers, null);
    }

    public static HttpRequest forPut(
            @NonNull String uriTemplate,
            @Nullable Charset uriEncoding,
            @Nullable MultiValueMap<String, Object> queryParams,
            @Nullable Map<String, Object> uriVariables,
            @Nullable Object body) {
        return forPut(uriTemplate, uriEncoding, queryParams, uriVariables, null, body);
    }

    public static HttpRequest forPut(
            @NonNull String uriTemplate,
            @Nullable Charset uriEncoding,
            @Nullable MultiValueMap<String, Object> queryParams,
            @Nullable Map<String, Object> uriVariables,
            @Nullable HttpHeaders headers,
            @Nullable Object body) {
        return create(HttpMethod.PUT, uriTemplate, uriEncoding, queryParams, uriVariables, headers, body);
    }

    public static HttpRequest forPatch(
            @NonNull String uriTemplate,
            @Nullable Charset uriEncoding,
            @Nullable MultiValueMap<String, Object> queryParams,
            @Nullable Map<String, Object> uriVariables,
            @Nullable Object body) {
        return forPatch(uriTemplate, uriEncoding, queryParams, uriVariables, null, body);
    }

    public static HttpRequest forPatch(
            @NonNull String uriTemplate,
            @Nullable Charset uriEncoding,
            @Nullable MultiValueMap<String, Object> queryParams,
            @Nullable Map<String, Object> uriVariables,
            @Nullable HttpHeaders headers,
            @Nullable Object body) {
        return create(HttpMethod.PATCH, uriTemplate, uriEncoding, queryParams, uriVariables, headers, body);
    }

    public static HttpRequest forPost(
            @NonNull String uriTemplate,
            @Nullable Charset uriEncoding,
            @Nullable MultiValueMap<String, Object> queryParams,
            @Nullable Map<String, Object> uriVariables,
            @Nullable Object body
    ) {
        return forPost(uriTemplate, uriEncoding, queryParams, uriVariables, null, body);
    }

    public static HttpRequest forPost(
            @NonNull String uriTemplate,
            @Nullable Charset uriEncoding,
            @Nullable MultiValueMap<String, Object> queryParams,
            @Nullable Map<String, Object> uriVariables,
            @Nullable HttpHeaders headers,
            @Nullable Object body
    ) {
        return create(HttpMethod.POST, uriTemplate, uriEncoding, queryParams, uriVariables, headers, body);
    }

    public static HttpRequest forPostMultiParts(
            @NonNull String uriTemplate,
            @Nullable Charset uriEncoding,
            @Nullable MultiValueMap<String, Object> queryParams,
            @Nullable Map<String, Object> uriVariables,
            MultiPartsForm body
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return create(HttpMethod.POST, uriTemplate, uriEncoding, queryParams, uriVariables, headers, body);
    }

    public static HttpRequest forPostMultiParts(
            @NonNull String uriTemplate,
            @Nullable Charset uriEncoding,
            @Nullable MultiValueMap<String, Object> queryParams,
            @Nullable Map<String, Object> uriVariables,
            MultiValueMap<String, Object> body
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return create(HttpMethod.POST, uriTemplate, uriEncoding, queryParams, uriVariables, headers, body);
    }

    public static HttpRequest forPostMultiParts(
            @NonNull String uriTemplate,
            @Nullable Charset uriEncoding,
            @Nullable MultiValueMap<String, Object> queryParams,
            @Nullable Map<String, Object> uriVariables,
            Map<String, Object> body
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return create(HttpMethod.POST, uriTemplate, uriEncoding, queryParams, uriVariables, headers, body);
    }
}
