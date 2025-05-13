package com.jn.agileway.httpclient.core;

import com.jn.agileway.httpclient.core.multipart.MultiPartsForm;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.collection.multivalue.MultiValueMap;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.net.uri.component.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

public class HttpRequest<T> {
    private URI uri;
    private HttpMethod method;
    private HttpHeaders headers;
    private T content;


    HttpRequest(URI uri, HttpMethod method, HttpHeaders headers, T body) {
        this.uri = uri;
        this.method = method;
        this.headers = headers == null ? new HttpHeaders() : headers;
        this.content = body;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public static HttpRequest create(@NonNull HttpMethod method,
                                     @NonNull String uriTemplate,
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
        URI uri = uriBuilder.build().toUri();

        return new HttpRequest(uri, method, headers, body);
    }

    public static HttpRequest forGet(
            @NonNull String uriTemplate,
            @Nullable MultiValueMap<String, Object> queryParams,
            @Nullable Map<String, Object> uriVariables,
            @Nullable HttpHeaders headers) {
        return create(HttpMethod.GET, uriTemplate, queryParams, uriVariables, headers, null);
    }

    public static HttpRequest forDelete(
            @NonNull String uriTemplate,
            @Nullable MultiValueMap<String, Object> queryParams,
            @Nullable Map<String, Object> uriVariables,
            @Nullable HttpHeaders headers) {
        return create(HttpMethod.DELETE, uriTemplate, queryParams, uriVariables, headers, null);
    }

    public static HttpRequest forPut(
            @NonNull String uriTemplate,
            @Nullable MultiValueMap<String, Object> queryParams,
            @Nullable Map<String, Object> uriVariables,
            @Nullable HttpHeaders headers,
            @Nullable Object body) {
        return create(HttpMethod.PUT, uriTemplate, queryParams, uriVariables, headers, body);
    }

    public static HttpRequest forPatch(
            @NonNull String uriTemplate,
            @Nullable MultiValueMap<String, Object> queryParams,
            @Nullable Map<String, Object> uriVariables,
            @Nullable HttpHeaders headers,
            @Nullable Object body) {
        return create(HttpMethod.PATCH, uriTemplate, queryParams, uriVariables, headers, body);
    }

    public static HttpRequest forPost(
            @NonNull String uriTemplate,
            @Nullable MultiValueMap<String, Object> queryParams,
            @Nullable Map<String, Object> uriVariables,
            @Nullable HttpHeaders headers,
            @Nullable Object body
    ) {
        return create(HttpMethod.POST, uriTemplate, queryParams, uriVariables, headers, body);
    }

    public static HttpRequest forPostMultiParts(
            @NonNull String uriTemplate,
            @Nullable MultiValueMap<String, Object> queryParams,
            @Nullable Map<String, Object> uriVariables,
            @Nullable HttpHeaders headers,
            MultiPartsForm body
    ) {
        return create(HttpMethod.HEAD, uriTemplate, queryParams, uriVariables, headers, body);
    }

    public static HttpRequest forPostMultiParts(
            @NonNull String uriTemplate,
            @Nullable MultiValueMap<String, Object> queryParams,
            @Nullable Map<String, Object> uriVariables,
            @Nullable HttpHeaders headers,
            MultiValueMap<String, Object> body
    ) {
        return create(HttpMethod.HEAD, uriTemplate, queryParams, uriVariables, headers, body);
    }

    public static HttpRequest forPostMultiParts(
            @NonNull String uriTemplate,
            @Nullable MultiValueMap<String, Object> queryParams,
            @Nullable Map<String, Object> uriVariables,
            @Nullable HttpHeaders headers,
            Map<String, Object> body
    ) {
        return create(HttpMethod.HEAD, uriTemplate, queryParams, uriVariables, headers, body);
    }
}
