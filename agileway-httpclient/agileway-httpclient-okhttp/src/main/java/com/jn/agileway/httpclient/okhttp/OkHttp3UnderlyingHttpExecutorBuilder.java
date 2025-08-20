package com.jn.agileway.httpclient.okhttp;

import com.jn.agileway.httpclient.core.HttpProtocolVersion;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpExecutor;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpExecutorBuilder;
import com.jn.langx.security.ssl.SSLContextBuilder;
import com.jn.langx.util.collection.Lists;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

import javax.net.ssl.HostnameVerifier;
import java.net.Proxy;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class OkHttp3UnderlyingHttpExecutorBuilder implements UnderlyingHttpExecutorBuilder {
    private int connectTimeoutMills = -1;
    private int readTimeoutMills = -1;
    private int poolMaxIdleConnections = 5;
    private int keepAliveDurationMills = 5 * 60 * 1000;
    private Proxy proxy;
    private SSLContextBuilder sslContextBuilder;
    private HostnameVerifier hostnameVerifier;

    private int workerThreads;

    private HttpProtocolVersion protocolVersion;
    @Override
    public String getName() {
        return "okhttp3";
    }

    @Override
    public UnderlyingHttpExecutorBuilder protocolVersion(HttpProtocolVersion protocolVersion) {
        this.protocolVersion = protocolVersion;
        return this;
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
    public UnderlyingHttpExecutorBuilder workThreads(int workerThreads) {
        this.workerThreads = workerThreads;
        return this;
    }


    private static final List<HttpProtocolVersion> supportedProtocols = Lists.newArrayList(
            HttpProtocolVersion.HTTP_1_1,
            HttpProtocolVersion.HTTP_2
    );

    @Override
    public List<HttpProtocolVersion> supportedProtocols() {
        return supportedProtocols;
    }

    @Override
    public int minJdkVersion() {
        return 8;
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
        if (workerThreads > 0) {
            builder.dispatcher(new Dispatcher(new ThreadPoolExecutor(workerThreads, workerThreads, 120L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>())));
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

    public UnderlyingHttpExecutorBuilder get() {
        return new OkHttp3UnderlyingHttpExecutorBuilder();
    }
}
