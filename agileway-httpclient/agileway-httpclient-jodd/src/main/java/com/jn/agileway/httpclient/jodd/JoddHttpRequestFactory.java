package com.jn.agileway.httpclient.jodd;

import com.jn.agileway.httpclient.core.AbstractUnderlyingHttpRequestFactory;
import com.jn.agileway.httpclient.core.UnderlyingHttpRequest;
import com.jn.agileway.httpclient.core.UnderlyingHttpRequestFactory;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;
import jodd.http.HttpConnectionProvider;
import jodd.http.ProxyInfo;
import jodd.http.net.SSLSocketHttpConnectionProvider;
import jodd.http.net.SocketHttpConnectionProvider;

import java.net.URI;
import java.security.NoSuchAlgorithmException;

public class JoddHttpRequestFactory extends AbstractUnderlyingHttpRequestFactory {

    private ProxyInfo proxyInfo;


    @Override
    public UnderlyingHttpRequest create(HttpMethod method, URI uri, HttpHeaders httpHeaders) throws Exception {
        boolean streamMode = HttpClientUtils.requestBodyUseStreamMode(method, httpHeaders);
        return new JoddHttpRequest(createJoddHttpRequest(method, uri), streamMode);
    }

    private jodd.http.HttpRequest createJoddHttpRequest(HttpMethod method, URI uri) {
        jodd.http.HttpRequest request = jodd.http.HttpRequest.create(method.name(), uri.toString())
                .withConnectionProvider(createConnectionProvider(uri));
        if (connectTimeoutMills > 0) {
            request.connectionTimeout(connectTimeoutMills);
        }
        if (readTimeoutMills > 0) {
            request.timeout(readTimeoutMills);
        }
        request.followRedirects(method == HttpMethod.GET);
        return request;
    }

    private HttpConnectionProvider createConnectionProvider(URI uri) {
        boolean isSslEnabled = HttpClientUtils.isSSLEnabled(uri);
        HttpConnectionProvider connectionProvider = isSslEnabled ? new SocketHttpConnectionProvider() : new SSLSocketHttpConnectionProvider(getSslSocketFactory());
        if (proxyInfo != null) {
            connectionProvider.useProxy(proxyInfo);
        }
        return connectionProvider;
    }
}
