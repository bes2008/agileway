package com.jn.agileway.httpclient.jdk11;

import com.jn.agileway.httpclient.core.UnderlyingHttpRequestFactory;
import com.jn.agileway.httpclient.core.UnderlyingHttpRequestFactoryBuilder;
import com.jn.langx.security.ssl.SSLContextBuilder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.ExecutorService;

public class Jdk11UnderlyingHttpRequestFactoryBuilder implements UnderlyingHttpRequestFactoryBuilder {
    private int connectTimeoutMills = 5000;
    private SSLContextBuilder sslContextBuilder;
    private Proxy proxy;
    private int readTimeoutMills = 5000;

    private ExecutorService executor;

    @Override
    public Jdk11UnderlyingHttpRequestFactoryBuilder poolMaxIdleConnections(int maxIdleConnections) {
        return this;
    }

    @Override
    public String getName() {
        return "jdk11";
    }

    @Override
    public Jdk11UnderlyingHttpRequestFactoryBuilder keepAliveDurationMills(int keepAliveDurationInMills) {
        return this;
    }

    @Override
    public Jdk11UnderlyingHttpRequestFactoryBuilder connectTimeoutMills(int connectTimeoutInMills) {
        this.connectTimeoutMills = connectTimeoutInMills;
        return this;
    }

    @Override
    public Jdk11UnderlyingHttpRequestFactoryBuilder readTimeoutMills(int readTimeoutInMills) {
        this.readTimeoutMills = readTimeoutInMills;
        return this;
    }

    @Override
    public Jdk11UnderlyingHttpRequestFactoryBuilder proxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    @Override
    public Jdk11UnderlyingHttpRequestFactoryBuilder hostnameVerifier(HostnameVerifier hostnameVerifier) {
        return this;
    }

    @Override
    public Jdk11UnderlyingHttpRequestFactoryBuilder sslContextBuilder(SSLContextBuilder sslContextBuilder) {
        this.sslContextBuilder = sslContextBuilder;
        return this;
    }

    @Override
    public Jdk11UnderlyingHttpRequestFactoryBuilder executor(ExecutorService executor) {
        this.executor = executor;
        return this;
    }

    @Override
    public UnderlyingHttpRequestFactory build() {
        HttpClient.Builder builder = HttpClient.newBuilder();
        builder.connectTimeout(Duration.ofMillis(connectTimeoutMills));
        if (sslContextBuilder != null) {
            SSLContext sslContext = sslContextBuilder.build();
            builder.sslContext(sslContext);
        }
        if (proxy != null) {
            builder.proxy(ProxySelector.of((InetSocketAddress) proxy.address()));
        }
        if (executor != null) {
            builder.executor(executor);
        }

        HttpClient httpClient = builder.build();
        Jdk11UnderlyingHttpRequestFactory factory = new Jdk11UnderlyingHttpRequestFactory();
        factory.setHttpClient(httpClient);

        int timeoutMills = this.connectTimeoutMills + this.readTimeoutMills * 3;
        factory.setTimeout(Duration.ofMillis(timeoutMills));
        return factory;
    }
}
