package com.jn.agileway.httpclient.okhttp;

import com.jn.agileway.httpclient.core.UnderlyingHttpRequestFactoryBuilder;
import com.jn.langx.security.ssl.SSLContextBuilder;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

import javax.net.ssl.HostnameVerifier;
import java.net.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class OkHttp3UnderlyingHttpRequestFactoryBuilder implements UnderlyingHttpRequestFactoryBuilder {
    private int connectTimeoutMills = -1;
    private int readTimeoutMills = -1;
    private int poolMaxIdleConnections = 5;
    private int keepAliveDurationMills = 5 * 60 * 1000;
    private Proxy proxy;
    private SSLContextBuilder sslContextBuilder;
    private HostnameVerifier hostnameVerifier;

    private ExecutorService executor;

    @Override
    public OkHttp3UnderlyingHttpRequestFactoryBuilder poolMaxIdleConnections(int maxIdleConnections) {
        this.poolMaxIdleConnections = maxIdleConnections;
        return this;
    }

    @Override
    public OkHttp3UnderlyingHttpRequestFactoryBuilder keepAliveDurationMills(int keepAliveDurationInMills) {
        this.keepAliveDurationMills = keepAliveDurationInMills;
        return this;
    }

    @Override
    public OkHttp3UnderlyingHttpRequestFactoryBuilder connectTimeoutMills(int connectTimeoutInMills) {
        this.connectTimeoutMills = connectTimeoutInMills;
        return this;
    }

    @Override
    public OkHttp3UnderlyingHttpRequestFactoryBuilder readTimeoutMills(int readTimeoutInMills) {
        this.readTimeoutMills = readTimeoutInMills;
        return this;
    }

    @Override
    public OkHttp3UnderlyingHttpRequestFactoryBuilder proxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    @Override
    public OkHttp3UnderlyingHttpRequestFactoryBuilder hostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    @Override
    public OkHttp3UnderlyingHttpRequestFactoryBuilder sslContextBuilder(SSLContextBuilder sslContextBuilder) {
        this.sslContextBuilder = sslContextBuilder;
        return this;
    }

    @Override
    public UnderlyingHttpRequestFactoryBuilder executor(ExecutorService executor) {
        this.executor = executor;
        return this;
    }

    @Override
    public OkHttp3UnderlyingHttpRequestFactory build() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(poolMaxIdleConnections, keepAliveDurationMills, TimeUnit.MILLISECONDS))
                .readTimeout(readTimeoutMills, TimeUnit.MILLISECONDS)
                .connectTimeout(connectTimeoutMills, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true);
        if (sslContextBuilder != null) {
            builder.sslSocketFactory(sslContextBuilder.build().getSocketFactory());
        }
        if (executor != null) {
            builder.dispatcher(new Dispatcher(executor));
        }
        if (proxy != null) {
            builder.proxy(proxy);
        }
        if (hostnameVerifier != null) {
            builder.hostnameVerifier(hostnameVerifier);
        }
        OkHttpClient okHttpClient = builder.build();
        OkHttp3UnderlyingHttpRequestFactory result = new OkHttp3UnderlyingHttpRequestFactory();
        result.setHttpClient(okHttpClient);
        return result;
    }
}
