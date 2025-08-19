package com.jn.agileway.httpclient.apachehttpclient5;

import com.jn.agileway.httpclient.core.HttpProtocolVersion;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpExecutor;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpExecutorBuilder;
import com.jn.langx.security.ssl.SSLContextBuilder;
import com.jn.langx.util.collection.Lists;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.core5.http.HttpVersion;
import org.apache.hc.core5.http.config.Http1Config;

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
        return this;
    }

    @Override
    public ApacheUnderlyingHttpExecutorBuilder keepAliveDurationMills(int keepAliveDurationInMills) {
        return this;
    }

    @Override
    public ApacheUnderlyingHttpExecutorBuilder connectTimeoutMills(int connectTimeoutInMills) {
        return this;
    }

    @Override
    public ApacheUnderlyingHttpExecutorBuilder requestTimeoutMills(int readTimeoutInMills) {
        return this;
    }

    @Override
    public ApacheUnderlyingHttpExecutorBuilder proxy(Proxy proxy) {
        return this;
    }

    @Override
    public ApacheUnderlyingHttpExecutorBuilder hostnameVerifier(HostnameVerifier hostnameVerifier) {
        return this;
    }

    @Override
    public ApacheUnderlyingHttpExecutorBuilder sslContextBuilder(SSLContextBuilder sslContextBuilder) {
        return this;
    }

    @Override
    public ApacheUnderlyingHttpExecutorBuilder executor(ExecutorService executor) {
        return this;
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
        CloseableHttpAsyncClient httpClient = protocolVersion == HttpProtocolVersion.HTTP_2 ? buildHttp2AsyncHttpClient() : buildHttp11AsyncHttpClient();
        return new ApacheUnderlyingHttpExecutor(httpClient);
    }

    private CloseableHttpAsyncClient buildHttp2AsyncHttpClient() {
        return HttpAsyncClients.customHttp2()
                .build();
    }

    private CloseableHttpAsyncClient buildHttp11AsyncHttpClient() {
        return HttpAsyncClients.custom()
                .setHttp1Config(Http1Config.custom().setVersion(HttpVersion.HTTP_1_1).build())

                .build();
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
