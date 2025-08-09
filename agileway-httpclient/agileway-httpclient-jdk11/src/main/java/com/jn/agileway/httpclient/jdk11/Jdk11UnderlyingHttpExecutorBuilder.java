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
import java.util.concurrent.ExecutorService;

public class Jdk11UnderlyingHttpExecutorBuilder implements UnderlyingHttpExecutorBuilder {
    private int connectTimeoutMills = 5000;
    private SSLContextBuilder sslContextBuilder;
    private Proxy proxy;
    private int readTimeoutMills = 5000;

    private ExecutorService executor;

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
    public Jdk11UnderlyingHttpExecutorBuilder executor(ExecutorService executor) {
        this.executor = executor;
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
        if (executor != null) {
            builder.executor(executor);
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
}
