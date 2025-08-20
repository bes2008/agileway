package com.jn.agileway.httpclient.hc4.ext;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;

public interface HttpAsyncClientCustomizer {
    void customizeHttpRequest(RequestConfig.Builder requestConfigBuilder);

    void customizeAsyncHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder);
}
