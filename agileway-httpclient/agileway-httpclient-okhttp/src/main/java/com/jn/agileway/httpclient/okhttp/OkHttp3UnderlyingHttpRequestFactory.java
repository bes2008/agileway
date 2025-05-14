package com.jn.agileway.httpclient.okhttp;

import com.jn.agileway.httpclient.core.UnderlyingHttpRequest;
import com.jn.agileway.httpclient.core.UnderlyingHttpRequestFactory;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;
import okhttp3.OkHttpClient;

import java.net.URI;

public class OkHttp3UnderlyingHttpRequestFactory implements UnderlyingHttpRequestFactory {
    private OkHttpClient httpClient;

    public void setHttpClient(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public UnderlyingHttpRequest create(HttpMethod method, URI uri, HttpHeaders httpHeaders) throws Exception {
        return new OkHttp3UnderlyingHttpRequest(method, uri, httpHeaders, httpClient);
    }
}
