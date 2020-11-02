package com.jn.agileway.httpclient;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;

public interface HttpAsyncClientCustomizer {
    void customizeHttpRequest(RequestConfig.Builder requestConfigBuilder);

    void customizeAsyncHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder);
}
