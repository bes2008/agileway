package com.jn.agileway.httpclient.apachehttpclient5;

import com.jn.agileway.httpclient.core.HttpProtocolVersion;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpExecutor;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpExecutorBuilder;
import com.jn.langx.security.ssl.SSLContextBuilder;
import com.jn.langx.util.collection.Lists;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;

import javax.net.ssl.HostnameVerifier;
import java.net.Proxy;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class ApacheUnderlyingHttpExecutorBuilder implements UnderlyingHttpExecutorBuilder {

    private HttpProtocolVersion protocolVersion;

    @Override
    public UnderlyingHttpExecutorBuilder protocolVersion(HttpProtocolVersion protocolVersion) {
        this.protocolVersion = protocolVersion;
        return this;
    }

    @Override
    public ApacheUnderlyingHttpExecutorBuilder poolMaxIdleConnections(int maxIdleConnections) {
        return null;
    }

    @Override
    public ApacheUnderlyingHttpExecutorBuilder keepAliveDurationMills(int keepAliveDurationInMills) {
        return null;
    }

    @Override
    public ApacheUnderlyingHttpExecutorBuilder connectTimeoutMills(int connectTimeoutInMills) {
        return null;
    }

    @Override
    public ApacheUnderlyingHttpExecutorBuilder requestTimeoutMills(int readTimeoutInMills) {
        return null;
    }

    @Override
    public ApacheUnderlyingHttpExecutorBuilder proxy(Proxy proxy) {
        return null;
    }

    @Override
    public ApacheUnderlyingHttpExecutorBuilder hostnameVerifier(HostnameVerifier hostnameVerifier) {
        return null;
    }

    @Override
    public ApacheUnderlyingHttpExecutorBuilder sslContextBuilder(SSLContextBuilder sslContextBuilder) {
        return null;
    }

    @Override
    public ApacheUnderlyingHttpExecutorBuilder executor(ExecutorService executor) {
        return null;
    }

    private static final List<HttpProtocolVersion> supportedProtocols = Lists.newArrayList(
            HttpProtocolVersion.HTTP_1_1, HttpProtocolVersion.HTTP_2
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

        CloseableHttpAsyncClient asyncClient = HttpAsyncClients.custom()
                .build();

        return null;
    }

    @Override
    public String getName() {
        return "apache-httpclient5";
    }

    @Override
    public ApacheUnderlyingHttpExecutorBuilder get() {
        return null;
    }
}
