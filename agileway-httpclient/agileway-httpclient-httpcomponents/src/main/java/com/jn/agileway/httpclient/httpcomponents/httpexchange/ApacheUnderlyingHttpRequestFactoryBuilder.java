package com.jn.agileway.httpclient.httpcomponents.httpexchange;

import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpRequestFactory;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpRequestFactoryBuilder;
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

public class ApacheUnderlyingHttpRequestFactoryBuilder implements UnderlyingHttpRequestFactoryBuilder {
    private HttpClientProperties config = new HttpClientProperties();
    private HostnameVerifier hostnameVerifier;
    private SSLContextBuilder sslContextBuilder;
    private Proxy proxy;
    private ExecutorService executor;

    @Override
    public ApacheUnderlyingHttpRequestFactoryBuilder poolMaxIdleConnections(int maxIdleConnections) {
        config.setPoolMaxIdleConnections(maxIdleConnections);
        return this;
    }

    @Override
    public String getName() {
        return "apache-httpcomponents";
    }

    @Override
    public ApacheUnderlyingHttpRequestFactoryBuilder keepAliveDurationMills(int keepAliveDurationInMills) {
        config.setKeepAliveTimeoutInMills(keepAliveDurationInMills);
        return this;
    }

    @Override
    public ApacheUnderlyingHttpRequestFactoryBuilder connectTimeoutMills(int connectTimeoutInMills) {
        config.setConnectTimeoutInMills(connectTimeoutInMills);
        return this;
    }

    @Override
    public ApacheUnderlyingHttpRequestFactoryBuilder readTimeoutMills(int readTimeoutInMills) {
        config.setSocketTimeoutInMills(readTimeoutInMills);
        return this;
    }

    @Override
    public ApacheUnderlyingHttpRequestFactoryBuilder proxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    @Override
    public ApacheUnderlyingHttpRequestFactoryBuilder hostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    @Override
    public ApacheUnderlyingHttpRequestFactoryBuilder sslContextBuilder(SSLContextBuilder sslContextBuilder) {
        this.sslContextBuilder = sslContextBuilder;
        return this;
    }

    public ApacheUnderlyingHttpRequestFactoryBuilder executor(ExecutorService executor) {
        this.executor = executor;
        return this;
    }

    @Override
    public UnderlyingHttpRequestFactory build() {
        ApacheUnderlyingHttpRequestFactory factory = new ApacheUnderlyingHttpRequestFactory();
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
                    if (ApacheUnderlyingHttpRequestFactoryBuilder.this.hostnameVerifier != null) {
                        httpClientBuilder.setSSLHostnameVerifier(ApacheUnderlyingHttpRequestFactoryBuilder.this.hostnameVerifier);
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
                    Proxy javaProxy = ApacheUnderlyingHttpRequestFactoryBuilder.this.proxy;
                    InetSocketAddress proxyAddress = (InetSocketAddress) javaProxy.address();
                    HttpHost httpHost = new HttpHost(proxyAddress.getHostName(), proxyAddress.getPort(), sslContextBuilder != null ? "https" : "http");
                    httpClientBuilder.setProxy(httpHost);
                }
            });
        }
        clientProvider.setCustomizers(customizers);
        clientProvider.startup();
        factory.setHttpClientProvider(clientProvider);
        return factory;
    }
}
