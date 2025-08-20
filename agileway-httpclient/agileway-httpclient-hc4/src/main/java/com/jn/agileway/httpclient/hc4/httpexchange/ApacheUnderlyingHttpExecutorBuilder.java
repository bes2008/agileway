package com.jn.agileway.httpclient.hc4.httpexchange;

import com.jn.agileway.httpclient.core.HttpProtocolVersion;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpExecutor;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpExecutorBuilder;
import com.jn.agileway.httpclient.hc4.ext.HttpClientCustomizer;
import com.jn.agileway.httpclient.hc4.ext.HttpClientProperties;
import com.jn.agileway.httpclient.hc4.ext.HttpClientProvider;
import com.jn.langx.security.ssl.SSLContextBuilder;
import com.jn.langx.util.collection.Lists;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

public class ApacheUnderlyingHttpExecutorBuilder implements UnderlyingHttpExecutorBuilder {
    private HttpClientProperties config = new HttpClientProperties();
    private HostnameVerifier hostnameVerifier;
    private SSLContextBuilder sslContextBuilder;
    private Proxy proxy;

    @Override
    public UnderlyingHttpExecutorBuilder protocolVersion(HttpProtocolVersion protocolVersion) {
        return this;
    }

    @Override
    public ApacheUnderlyingHttpExecutorBuilder poolMaxIdleConnections(int maxIdleConnections) {
        config.setPoolMaxIdleConnections(maxIdleConnections);
        return this;
    }

    @Override
    public String getName() {
        return "apache-httpclient4";
    }

    @Override
    public ApacheUnderlyingHttpExecutorBuilder keepAliveDurationMills(int keepAliveDurationInMills) {
        config.setKeepAliveTimeoutInMills(keepAliveDurationInMills);
        return this;
    }

    @Override
    public ApacheUnderlyingHttpExecutorBuilder connectTimeoutMills(int connectTimeoutInMills) {
        config.setConnectTimeoutInMills(connectTimeoutInMills);
        return this;
    }

    @Override
    public ApacheUnderlyingHttpExecutorBuilder requestTimeoutMills(int readTimeoutInMills) {
        config.setSocketTimeoutInMills(readTimeoutInMills);
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

    public ApacheUnderlyingHttpExecutorBuilder workThreads(int workerThreads) {
        return this;
    }

    private static final List<HttpProtocolVersion> supportedProtocols = Lists.newArrayList(
            HttpProtocolVersion.HTTP_1_1
    );

    @Override
    public List<HttpProtocolVersion> supportedProtocols() {
        return supportedProtocols;
    }

    @Override
    public int minJdkVersion() {
        return 6;
    }

    @Override
    public UnderlyingHttpExecutor build() {
        ApacheUnderlyingHttpExecutor httpExecutor = new ApacheUnderlyingHttpExecutor();
        this.config.setMaxRetry(1);
        HttpClientProvider clientProvider = new HttpClientProvider();
        clientProvider.setConfig(this.config);
        List<HttpClientCustomizer> customizers = new ArrayList<>();
        if (sslContextBuilder != null) {
            SSLContext sslContext = sslContextBuilder.build();
            customizers.add(new HttpClientCustomizer() {
                @Override
                public void customizeHttpRequest(RequestConfig.Builder requestConfigBuilder) {

                }

                @Override
                public void customizeHttpClient(HttpClientBuilder httpClientBuilder) {
                    if (ApacheUnderlyingHttpExecutorBuilder.this.hostnameVerifier != null) {
                        httpClientBuilder.setSSLHostnameVerifier(ApacheUnderlyingHttpExecutorBuilder.this.hostnameVerifier);
                    }
                    httpClientBuilder.setSSLContext(sslContext);
                }
            });
        }
        if (proxy != null) {
            customizers.add(new HttpClientCustomizer() {
                @Override
                public void customizeHttpRequest(RequestConfig.Builder requestConfigBuilder) {
                }

                @Override
                public void customizeHttpClient(HttpClientBuilder httpClientBuilder) {
                    Proxy javaProxy = ApacheUnderlyingHttpExecutorBuilder.this.proxy;
                    InetSocketAddress proxyAddress = (InetSocketAddress) javaProxy.address();
                    HttpHost httpHost = new HttpHost(proxyAddress.getHostName(), proxyAddress.getPort(), sslContextBuilder != null ? "https" : "http");
                    httpClientBuilder.setProxy(httpHost);
                }
            });
        }
        clientProvider.setCustomizers(customizers);
        clientProvider.startup();
        httpExecutor.setHttpClient(clientProvider.get());
        return httpExecutor;
    }

    public UnderlyingHttpExecutorBuilder get() {
        return new ApacheUnderlyingHttpExecutorBuilder();
    }
}
