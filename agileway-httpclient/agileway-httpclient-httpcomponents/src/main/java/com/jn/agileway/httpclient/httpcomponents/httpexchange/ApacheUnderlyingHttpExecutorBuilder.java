package com.jn.agileway.httpclient.httpcomponents.httpexchange;

import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpExecutor;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpExecutorBuilder;
import com.jn.agileway.httpclient.httpcomponents.ext.HttpClientCustomizer;
import com.jn.agileway.httpclient.httpcomponents.ext.HttpClientProperties;
import com.jn.agileway.httpclient.httpcomponents.ext.HttpClientProvider;
import com.jn.langx.security.ssl.SSLContextBuilder;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class ApacheUnderlyingHttpExecutorBuilder implements UnderlyingHttpExecutorBuilder {
    private HttpClientProperties config = new HttpClientProperties();
    private HostnameVerifier hostnameVerifier;
    private SSLContextBuilder sslContextBuilder;
    private Proxy proxy;
    private ExecutorService executor;

    @Override
    public ApacheUnderlyingHttpExecutorBuilder poolMaxIdleConnections(int maxIdleConnections) {
        config.setPoolMaxIdleConnections(maxIdleConnections);
        return this;
    }

    @Override
    public String getName() {
        return "apache-httpcomponents";
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
    public ApacheUnderlyingHttpExecutorBuilder readTimeoutMills(int readTimeoutInMills) {
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

    public ApacheUnderlyingHttpExecutorBuilder executor(ExecutorService executor) {
        this.executor = executor;
        return this;
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
}
