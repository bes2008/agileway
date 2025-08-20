package com.jn.agileway.httpclient.jdk11;

import com.jn.agileway.httpclient.core.HttpProtocolVersion;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpExecutor;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpExecutorBuilder;
import com.jn.langx.security.ssl.SSLContextBuilder;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.timing.TimeDuration;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Jdk11UnderlyingHttpExecutorBuilder implements UnderlyingHttpExecutorBuilder {
    private int connectTimeoutMills = 5000;
    private SSLContextBuilder sslContextBuilder;
    private Proxy proxy;
    private int readTimeoutMills = 5000;

    private int workerThreads;

    private HttpProtocolVersion protocolVersion;

    @Override
    public UnderlyingHttpExecutorBuilder protocolVersion(HttpProtocolVersion protocolVersion) {
        this.protocolVersion = protocolVersion;
        return this;
    }

    @Override
    public Jdk11UnderlyingHttpExecutorBuilder poolMaxIdleConnections(int maxIdleConnections) {
        return this;
    }

    @Override
    public String getName() {
        return "jdk11";
    }

    @Override
    public Jdk11UnderlyingHttpExecutorBuilder keepAliveDurationMills(int keepAliveDurationInMills) {
        return this;
    }

    @Override
    public Jdk11UnderlyingHttpExecutorBuilder connectTimeoutMills(int connectTimeoutInMills) {
        this.connectTimeoutMills = connectTimeoutInMills;
        return this;
    }

    @Override
    public Jdk11UnderlyingHttpExecutorBuilder requestTimeoutMills(int readTimeoutInMills) {
        this.readTimeoutMills = readTimeoutInMills;
        return this;
    }

    @Override
    public Jdk11UnderlyingHttpExecutorBuilder proxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    @Override
    public Jdk11UnderlyingHttpExecutorBuilder hostnameVerifier(HostnameVerifier hostnameVerifier) {
        return this;
    }

    @Override
    public Jdk11UnderlyingHttpExecutorBuilder sslContextBuilder(SSLContextBuilder sslContextBuilder) {
        this.sslContextBuilder = sslContextBuilder;
        return this;
    }

    @Override
    public Jdk11UnderlyingHttpExecutorBuilder executor(int workerThreads) {
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
        return 11;
    }


    @Override
    public UnderlyingHttpExecutor build() {
        HttpClient.Builder builder = HttpClient.newBuilder();
        builder.connectTimeout(Duration.ofMillis(connectTimeoutMills));
        if (sslContextBuilder != null) {
            SSLContext sslContext = sslContextBuilder.build();
            builder.sslContext(sslContext);
        }
        if (proxy != null) {
            builder.proxy(ProxySelector.of((InetSocketAddress) proxy.address()));
        }
        if (workerThreads > 0) {
            builder.executor(new ThreadPoolExecutor(workerThreads, workerThreads, 120L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>()));
        }

        builder.version(HttpClient.Version.HTTP_1_1);

        builder.followRedirects(HttpClient.Redirect.NORMAL);

        HttpClient httpClient = builder.build();
        Jdk11UnderlyingHttpExecutor httpExecutor = new Jdk11UnderlyingHttpExecutor();
        httpExecutor.setHttpClient(httpClient);

        int timeoutMills = this.connectTimeoutMills + this.readTimeoutMills * 3;
        httpExecutor.setTimeout(TimeDuration.ofMillis(timeoutMills));
        return httpExecutor;
    }

    public UnderlyingHttpExecutorBuilder get() {
        return new Jdk11UnderlyingHttpExecutorBuilder();
    }
}
