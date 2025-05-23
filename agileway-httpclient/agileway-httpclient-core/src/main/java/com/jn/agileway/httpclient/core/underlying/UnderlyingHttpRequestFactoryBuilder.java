package com.jn.agileway.httpclient.core.underlying;

import com.jn.langx.Builder;
import com.jn.langx.Named;
import com.jn.langx.security.ssl.SSLContextBuilder;

import javax.net.ssl.HostnameVerifier;
import java.net.Proxy;
import java.util.concurrent.ExecutorService;

public interface UnderlyingHttpRequestFactoryBuilder extends Builder<UnderlyingHttpRequestFactory>, Named {

    UnderlyingHttpRequestFactoryBuilder poolMaxIdleConnections(int maxIdleConnections);

    UnderlyingHttpRequestFactoryBuilder keepAliveDurationMills(int keepAliveDurationInMills);

    UnderlyingHttpRequestFactoryBuilder connectTimeoutMills(int connectTimeoutInMills);

    UnderlyingHttpRequestFactoryBuilder readTimeoutMills(int readTimeoutInMills);

    UnderlyingHttpRequestFactoryBuilder proxy(Proxy proxy);

    UnderlyingHttpRequestFactoryBuilder hostnameVerifier(HostnameVerifier hostnameVerifier);

    UnderlyingHttpRequestFactoryBuilder sslContextBuilder(SSLContextBuilder sslContextBuilder);

    UnderlyingHttpRequestFactoryBuilder executor(ExecutorService executor);

    @Override
    UnderlyingHttpRequestFactory build();
}
