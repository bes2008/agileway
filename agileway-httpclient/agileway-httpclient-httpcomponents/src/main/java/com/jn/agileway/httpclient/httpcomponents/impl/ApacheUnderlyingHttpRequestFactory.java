package com.jn.agileway.httpclient.httpcomponents.impl;

import com.jn.agileway.httpclient.core.AbstractUnderlyingHttpRequestFactory;
import com.jn.agileway.httpclient.core.UnderlyingHttpRequest;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;

import java.net.URI;

public class ApacheUnderlyingHttpRequestFactory extends AbstractUnderlyingHttpRequestFactory {
    private HttpClient httpClient;


    @Override
    public UnderlyingHttpRequest create(HttpMethod method, URI uri, HttpHeaders httpHeaders) throws Exception {
        return null;
    }

    private HttpUriRequest createHttpUriRequest(HttpMethod method, URI uri, HttpHeaders httpHeaders) throws Exception {
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
