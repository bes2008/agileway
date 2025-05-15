package com.jn.agileway.httpclient.jdk;

import com.jn.agileway.httpclient.core.UnderlyingHttpRequestFactory;
import com.jn.agileway.httpclient.core.UnderlyingHttpRequestFactoryBuilder;
import com.jn.langx.security.ssl.SSLContextBuilder;

import javax.net.ssl.HostnameVerifier;
import java.lang.reflect.Proxy;

public class JdkUnderlyingHttpRequestFactoryBuilder implements UnderlyingHttpRequestFactoryBuilder {

    private int connectTimeoutInMills = 3000;
    private int readTimeoutInMills = 60000;
    private Proxy proxy;
    private HostnameVerifier hostnameVerifier;
    private SSLContextBuilder sslContextBuilder;

    @Override
    public UnderlyingHttpRequestFactoryBuilder poolMaxIdleConnections(int maxIdleConnections) {
        return this;
    }

    @Override
    public UnderlyingHttpRequestFactoryBuilder keepAliveDurationMills(int keepAliveDurationInMills) {
        return this;
    }

    @Override
    public UnderlyingHttpRequestFactoryBuilder connectTimeoutMills(int connectTimeoutInMills) {
        this.connectTimeoutInMills = connectTimeoutInMills;
        return this;
    }

    @Override
    public UnderlyingHttpRequestFactoryBuilder readTimeoutMills(int readTimeoutInMills) {
        this.readTimeoutInMills = readTimeoutInMills;
        return this;
    }

    @Override
    public UnderlyingHttpRequestFactoryBuilder proxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    @Override
    public UnderlyingHttpRequestFactoryBuilder hostnameVerifier(HostnameVerifier hostnameVerifier) {
        return this;
    }

    @Override
    public UnderlyingHttpRequestFactoryBuilder sslContextBuilder(SSLContextBuilder sslContextBuilder) {
        return this;
    }

    @Override
    public UnderlyingHttpRequestFactory build() {
        return new JdkUnderlyingHttpRequestFactory();
    }
}
