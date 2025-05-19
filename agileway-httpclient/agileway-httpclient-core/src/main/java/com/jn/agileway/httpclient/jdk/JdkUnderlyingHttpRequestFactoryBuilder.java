package com.jn.agileway.httpclient.jdk;

import com.jn.agileway.httpclient.core.UnderlyingHttpRequestFactory;
import com.jn.agileway.httpclient.core.UnderlyingHttpRequestFactoryBuilder;
import com.jn.langx.security.ssl.SSLContextBuilder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.net.Proxy;
import java.util.concurrent.ExecutorService;

public class JdkUnderlyingHttpRequestFactoryBuilder implements UnderlyingHttpRequestFactoryBuilder {

    private int connectTimeoutInMills = 3000;
    private int readTimeoutInMills = 60000;
    private Proxy proxy;
    private HostnameVerifier hostnameVerifier;
    private SSLContextBuilder sslContextBuilder;

    @Override
    public String getName() {
        return "jdk";
    }

    @Override
    public JdkUnderlyingHttpRequestFactoryBuilder poolMaxIdleConnections(int maxIdleConnections) {
        return this;
    }

    @Override
    public JdkUnderlyingHttpRequestFactoryBuilder keepAliveDurationMills(int keepAliveDurationInMills) {
        return this;
    }

    @Override
    public JdkUnderlyingHttpRequestFactoryBuilder connectTimeoutMills(int connectTimeoutInMills) {
        this.connectTimeoutInMills = connectTimeoutInMills;
        return this;
    }

    @Override
    public JdkUnderlyingHttpRequestFactoryBuilder readTimeoutMills(int readTimeoutInMills) {
        this.readTimeoutInMills = readTimeoutInMills;
        return this;
    }

    @Override
    public JdkUnderlyingHttpRequestFactoryBuilder proxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    @Override
    public JdkUnderlyingHttpRequestFactoryBuilder hostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    @Override
    public JdkUnderlyingHttpRequestFactoryBuilder sslContextBuilder(SSLContextBuilder sslContextBuilder) {
        this.sslContextBuilder = sslContextBuilder;
        return this;
    }

    @Override
    public JdkUnderlyingHttpRequestFactoryBuilder executor(ExecutorService executor) {
        return this;
    }

    @Override
    public UnderlyingHttpRequestFactory build() {
        JdkUnderlyingHttpRequestFactory factory = new JdkUnderlyingHttpRequestFactory();
        factory.setConnectTimeoutMills(connectTimeoutInMills);
        factory.setReadTimeoutMills(readTimeoutInMills);
        factory.setProxy(proxy);
        factory.setHostnameVerifier(hostnameVerifier);
        SSLContext sslContext = sslContextBuilder.build();
        factory.setSSLContext(sslContext);
        return factory;
    }
}
