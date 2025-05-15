package com.jn.agileway.httpclient.httpcomponents.impl;

import com.jn.agileway.httpclient.core.UnderlyingHttpRequestFactory;
import com.jn.agileway.httpclient.core.UnderlyingHttpRequestFactoryBuilder;
import com.jn.agileway.httpclient.httpcomponents.ext.HttpClientCustomizer;
import com.jn.agileway.httpclient.httpcomponents.ext.HttpClientProperties;
import com.jn.agileway.httpclient.httpcomponents.ext.HttpClientProvider;
import com.jn.langx.security.ssl.SSLContextBuilder;
import com.jn.langx.util.collection.Lists;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.net.Proxy;

public class ApacheUnderlyingHttpRequestFactoryBuilder implements UnderlyingHttpRequestFactoryBuilder {
    private HttpClientProperties config = new HttpClientProperties();
    private HostnameVerifier hostnameVerifier;
    private SSLContextBuilder sslContextBuilder;

    @Override
    public UnderlyingHttpRequestFactoryBuilder poolMaxIdleConnections(int maxIdleConnections) {
        config.setPoolMaxIdleConnections(maxIdleConnections);
        return this;
    }

    @Override
    public UnderlyingHttpRequestFactoryBuilder keepAliveDurationMills(int keepAliveDurationInMills) {
        config.setKeepAliveTimeoutInMills(keepAliveDurationInMills);
        return this;
    }

    @Override
    public UnderlyingHttpRequestFactoryBuilder connectTimeoutMills(int connectTimeoutInMills) {
        config.setConnectTimeoutInMills(connectTimeoutInMills);
        return this;
    }

    @Override
    public UnderlyingHttpRequestFactoryBuilder readTimeoutMills(int readTimeoutInMills) {
        config.setSocketTimeoutInMills(readTimeoutInMills);
        return this;
    }

    @Override
    public UnderlyingHttpRequestFactoryBuilder proxy(Proxy proxy) {

        return this;
    }

    @Override
    public UnderlyingHttpRequestFactoryBuilder hostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    @Override
    public UnderlyingHttpRequestFactoryBuilder sslContextBuilder(SSLContextBuilder sslContextBuilder) {
        this.sslContextBuilder = sslContextBuilder;
        return this;
    }

    @Override
    public UnderlyingHttpRequestFactory build() {
        ApacheUnderlyingHttpRequestFactory factory = new ApacheUnderlyingHttpRequestFactory();
        this.config.setMaxRetry(1);
        HttpClientProvider clientProvider = new HttpClientProvider();
        clientProvider.setConfig(this.config);
        if (sslContextBuilder != null) {
            SSLContext sslContext = sslContextBuilder.build();
            HttpClientCustomizer customizer = new HttpClientCustomizer() {
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
            };
            clientProvider.setCustomizers(Lists.newArrayList(customizer));
        }
        clientProvider.startup();
        factory.setHttpClientProvider(clientProvider);
        return factory;
    }
}
