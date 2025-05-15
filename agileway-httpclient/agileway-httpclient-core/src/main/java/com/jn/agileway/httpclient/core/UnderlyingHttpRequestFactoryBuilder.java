package com.jn.agileway.httpclient.core;

import com.jn.langx.Builder;
import com.jn.langx.security.ssl.SSLContextBuilder;

import javax.net.ssl.HostnameVerifier;
import java.lang.reflect.Proxy;

public interface UnderlyingHttpRequestFactoryBuilder extends Builder<UnderlyingHttpRequestFactory> {

    UnderlyingHttpRequestFactoryBuilder poolMaxIdleConnections(int maxIdleConnections);

    UnderlyingHttpRequestFactoryBuilder keepAliveDurationMills(int keepAliveDurationInMills);

    UnderlyingHttpRequestFactoryBuilder connectTimeoutMills(int connectTimeoutInMills);

    UnderlyingHttpRequestFactoryBuilder readTimeoutMills(int readTimeoutInMills);

    UnderlyingHttpRequestFactoryBuilder proxy(Proxy proxy);

    UnderlyingHttpRequestFactoryBuilder hostnameVerifier(HostnameVerifier hostnameVerifier);

    UnderlyingHttpRequestFactoryBuilder sslContextBuilder(SSLContextBuilder sslContextBuilder);

    @Override
    UnderlyingHttpRequestFactory build();
}
