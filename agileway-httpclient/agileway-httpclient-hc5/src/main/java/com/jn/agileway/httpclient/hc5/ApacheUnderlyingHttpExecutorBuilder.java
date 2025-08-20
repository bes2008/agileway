package com.jn.agileway.httpclient.hc5;

import com.jn.agileway.httpclient.core.HttpProtocolVersion;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpExecutor;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpExecutorBuilder;
import com.jn.langx.security.ssl.SSLContextBuilder;
import com.jn.langx.util.collection.Lists;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.config.TlsConfig;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.core5.http.HttpVersion;
import org.apache.hc.core5.http.config.Http1Config;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.http2.HttpVersionPolicy;
import org.apache.hc.core5.http2.config.H2Config;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

import javax.net.ssl.HostnameVerifier;
import java.net.Proxy;
import java.util.List;

public class ApacheUnderlyingHttpExecutorBuilder implements UnderlyingHttpExecutorBuilder {

    private HttpProtocolVersion protocolVersion;
    private int keepAliveDurationInMills;
    private int connectTimeoutInMills = -1;
    private int readTimeoutInMills;
    private Proxy proxy;
    private HostnameVerifier hostnameVerifier;
    private SSLContextBuilder sslContextBuilder;
    private int workerThreads;


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
        this.keepAliveDurationInMills = keepAliveDurationInMills;
        return this;
    }

    @Override
    public ApacheUnderlyingHttpExecutorBuilder connectTimeoutMills(int connectTimeoutInMills) {
        this.connectTimeoutInMills = connectTimeoutInMills;
        return this;
    }

    @Override
    public ApacheUnderlyingHttpExecutorBuilder requestTimeoutMills(int readTimeoutInMills) {
        this.readTimeoutInMills = readTimeoutInMills;
        return this;
    }

    @Override
    public ApacheUnderlyingHttpExecutorBuilder proxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    @Override
    public ApacheUnderlyingHttpExecutorBuilder hostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    @Override
    public ApacheUnderlyingHttpExecutorBuilder sslContextBuilder(SSLContextBuilder sslContextBuilder) {
        this.sslContextBuilder = sslContextBuilder;
        return this;
    }

    @Override
    public ApacheUnderlyingHttpExecutorBuilder workThreads(int workerThreads) {
        this.workerThreads = workerThreads;
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
        return new ApacheUnderlyingHttpExecutor();
    }

    private CloseableHttpAsyncClient buildHttp2AsyncHttpClient() {
        return HttpAsyncClients.customHttp2()
                .setH2Config(H2Config.custom().setCompressionEnabled(true).build())
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setAuthenticationEnabled(true)
                        .setConnectionKeepAlive(TimeValue.ofMilliseconds(keepAliveDurationInMills))
                        .setResponseTimeout(Timeout.ofMilliseconds(readTimeoutInMills))
                        .build())
                .setUserAgent("agileway-" + getName())
                .setIOReactorConfig(IOReactorConfig.custom()
                        .setSoTimeout(Timeout.ofMilliseconds(readTimeoutInMills + connectTimeoutInMills))
                        .setSoKeepAlive(true)
                        .setIoThreadCount(workerThreads)
                        .build())
                .evictIdleConnections(TimeValue.ofSeconds(60))
                .disableAutomaticRetries()
                .build();
    }

    private CloseableHttpAsyncClient buildHttp11AsyncHttpClient() {

        PoolingAsyncClientConnectionManagerBuilder connectionPoolManagerBuilder =
                PoolingAsyncClientConnectionManagerBuilder.create()
                        .setMaxConnTotal(500)
                        .setDefaultConnectionConfig(ConnectionConfig.custom()
                                .setConnectTimeout(Timeout.ofMilliseconds(connectTimeoutInMills))
                                .setSocketTimeout(Timeout.ofMilliseconds(readTimeoutInMills + connectTimeoutInMills))
                                .build());
        if (sslContextBuilder != null) {
            TlsStrategy tlsStrategy = new DefaultClientTlsStrategy(sslContextBuilder.build(), this.hostnameVerifier);
            connectionPoolManagerBuilder.setTlsStrategy(tlsStrategy);
            connectionPoolManagerBuilder.setTlsStrategy(tlsStrategy)
                    .setDefaultTlsConfig(TlsConfig.custom()
                            .setSupportedProtocols(TLS.V_1_2, TLS.V_1_3)
                            .setVersionPolicy(HttpVersionPolicy.FORCE_HTTP_1)
                            .setHandshakeTimeout(Timeout.ofSeconds(30))
                            .build());
        }

        PoolingAsyncClientConnectionManager connectionPoolManager = connectionPoolManagerBuilder.build();
        return HttpAsyncClients.custom()
                .setHttp1Config(Http1Config.custom().setVersion(HttpVersion.HTTP_1_1).build())
                .setConnectionManagerShared(true)
                .setConnectionManager(connectionPoolManager)
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setAuthenticationEnabled(true)
                        .setConnectionKeepAlive(TimeValue.ofMilliseconds(keepAliveDurationInMills))
                        .setResponseTimeout(Timeout.ofMilliseconds(readTimeoutInMills))
                        .build())
                .setUserAgent("agileway-" + getName())
                .evictIdleConnections(TimeValue.ofSeconds(60))
                .disableAutomaticRetries()
                .build();
    }

    @Override
    public String getName() {
        return "apache-httpclient5";
    }

    @Override
    public ApacheUnderlyingHttpExecutorBuilder get() {
        return new ApacheUnderlyingHttpExecutorBuilder();
    }
}
