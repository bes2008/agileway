package com.jn.agileway.httpclient;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;

public interface HttpClientCustomizer {
    void customizeHttpRequest(RequestConfig.Builder requestConfigBuilder);
    void customizeHttpClient(HttpClientBuilder httpClientBuilder);
}
