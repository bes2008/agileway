package com.jn.agileway.httpclient.jdk;

import com.jn.agileway.httpclient.core.UnderlyingHttpRequestFactory;
import com.jn.agileway.httpclient.core.UnderlyingHttpRequestFactoryBuilder;
import com.jn.langx.security.ssl.SSLContextBuilder;

import javax.net.ssl.HostnameVerifier;
import java.lang.reflect.Proxy;

public class JdkUnderlyingHttpRequestFactoryBuilder implements UnderlyingHttpRequestFactoryBuilder {
    @Override
    public UnderlyingHttpRequestFactoryBuilder poolMaxIdleConnections(int maxIdleConnections) {
        return null;
    }

    @Override
    public UnderlyingHttpRequestFactoryBuilder keepAliveDurationMills(int keepAliveDurationInMills) {
        return null;
    }

    @Override
    public UnderlyingHttpRequestFactoryBuilder connectTimeoutMills(int connectTimeoutInMills) {
        return null;
    }

    @Override
    public UnderlyingHttpRequestFactoryBuilder readTimeoutMills(int readTimeoutInMills) {
        return null;
    }

    @Override
    public UnderlyingHttpRequestFactoryBuilder proxy(Proxy proxy) {
        return null;
    }

    @Override
    public UnderlyingHttpRequestFactoryBuilder hostnameVerifier(HostnameVerifier hostnameVerifier) {
        return null;
    }

    @Override
    public UnderlyingHttpRequestFactoryBuilder sslContextBuilder(SSLContextBuilder sslContextBuilder) {
        return null;
    }

    @Override
    public UnderlyingHttpRequestFactory build() {
        return null;
    }
}
