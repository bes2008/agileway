package com.jn.agileway.httpclient.jdk11;

import com.jn.agileway.httpclient.core.UnderlyingHttpRequest;
import com.jn.agileway.httpclient.core.UnderlyingHttpRequestFactory;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.ExecutorService;

public class Jdk11UnderlyingHttpRequestFactory implements UnderlyingHttpRequestFactory {
    private HttpClient httpClient;
    private Duration timeout = Duration.ofMinutes(5);
    private ExecutorService executor;

    public Jdk11UnderlyingHttpRequestFactory() {

    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public ExecutorService getExecutor() {
        return executor;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

    public UnderlyingHttpRequest create(HttpMethod method, URI uri, HttpHeaders httpHeaders) throws Exception {
        return new Jdk11UnderlyingHttpRequest(method, uri, httpHeaders, httpClient, timeout);
    }
}
