package com.jn.agileway.httpclient.okhttp;

import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpExecutor;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpExecutorBuilder;
import com.jn.langx.security.ssl.SSLContextBuilder;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

import javax.net.ssl.HostnameVerifier;
import java.net.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class OkHttp3UnderlyingHttpExecutorBuilder implements UnderlyingHttpExecutorBuilder {
    private int connectTimeoutMills = -1;
    private int readTimeoutMills = -1;
    private int poolMaxIdleConnections = 5;
    private int keepAliveDurationMills = 5 * 60 * 1000;
    private Proxy proxy;
    private SSLContextBuilder sslContextBuilder;
    private HostnameVerifier hostnameVerifier;

    private ExecutorService executor;

    @Override
    public String getName() {
        return "okhttp3";
    }

    @Override
    public OkHttp3UnderlyingHttpExecutorBuilder poolMaxIdleConnections(int maxIdleConnections) {
        this.poolMaxIdleConnections = maxIdleConnections;
        return this;
    }

    @Override
    public OkHttp3UnderlyingHttpExecutorBuilder keepAliveDurationMills(int keepAliveDurationInMills) {
        this.keepAliveDurationMills = keepAliveDurationInMills;
        return this;
    }

    @Override
    public OkHttp3UnderlyingHttpExecutorBuilder connectTimeoutMills(int connectTimeoutInMills) {
        this.connectTimeoutMills = connectTimeoutInMills;
        return this;
    }

    @Override
    public OkHttp3UnderlyingHttpExecutorBuilder requestTimeoutMills(int readTimeoutInMills) {
        this.readTimeoutMills = readTimeoutInMills;
        return this;
    }

    @Override
    public OkHttp3UnderlyingHttpExecutorBuilder proxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    @Override
    public OkHttp3UnderlyingHttpExecutorBuilder hostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    @Override
    public OkHttp3UnderlyingHttpExecutorBuilder sslContextBuilder(SSLContextBuilder sslContextBuilder) {
        this.sslContextBuilder = sslContextBuilder;
        return this;
    }

    @Override
    public UnderlyingHttpExecutorBuilder executor(ExecutorService executor) {
        this.executor = executor;
        return this;
    }

    @Override
    public UnderlyingHttpExecutor build() {

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
        OkHttp3UnderlyingHttpExecutor httpExecutor = new OkHttp3UnderlyingHttpExecutor();
        httpExecutor.setHttpClient(okHttpClient);
        return httpExecutor;
    }
}
