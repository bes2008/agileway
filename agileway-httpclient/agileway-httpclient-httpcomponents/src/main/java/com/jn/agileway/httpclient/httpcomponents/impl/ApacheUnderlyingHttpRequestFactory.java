package com.jn.agileway.httpclient.httpcomponents.impl;

import com.jn.agileway.httpclient.core.UnderlyingHttpRequest;
import com.jn.agileway.httpclient.core.UnderlyingHttpRequestFactory;
import com.jn.agileway.httpclient.httpcomponents.ext.HttpClientProvider;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;
import org.apache.http.client.methods.*;

import java.net.URI;

public class ApacheUnderlyingHttpRequestFactory implements UnderlyingHttpRequestFactory {
    private HttpClientProvider httpClientProvider;

    public void setHttpClientProvider(HttpClientProvider httpClient) {
        this.httpClientProvider = httpClient;
        httpClient.startup();
    }

    @Override
    public UnderlyingHttpRequest create(HttpMethod method, URI uri, HttpHeaders httpHeaders) throws Exception {
        HttpUriRequest request = createHttpUriRequest(method, uri);
        return new ApacheUnderlyingHttpRequest(this.httpClientProvider.get(), request, httpHeaders);
    }

    private HttpUriRequest createHttpUriRequest(HttpMethod method, URI uri) throws Exception {
        HttpUriRequest request = null;
        switch (method) {
            case GET:
                request = new HttpGet(uri);
                break;
            case POST:
                request = new HttpPost(uri);
                break;
            case PUT:
                request = new HttpPut(uri);
                break;
            case DELETE:
                request = new HttpDelete(uri);
                break;
            case HEAD:
                request = new HttpHead(uri);
                break;
            case OPTIONS:
                request = new HttpOptions(uri);
                break;
            case PATCH:
                request = new HttpPatch(uri);
                break;
            case TRACE:
            default:
                request = new HttpTrace(uri);
                break;
        }

        return request;
    }

}
