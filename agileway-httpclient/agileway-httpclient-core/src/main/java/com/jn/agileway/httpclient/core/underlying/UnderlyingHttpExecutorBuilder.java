package com.jn.agileway.httpclient.core.underlying;

import com.jn.langx.Builder;
import com.jn.langx.Named;
import com.jn.langx.security.ssl.SSLContextBuilder;

import javax.net.ssl.HostnameVerifier;
import java.net.Proxy;
import java.util.concurrent.ExecutorService;

public interface UnderlyingHttpExecutorBuilder extends Builder<UnderlyingHttpExecutor>, Named {

    UnderlyingHttpExecutorBuilder poolMaxIdleConnections(int maxIdleConnections);

    UnderlyingHttpExecutorBuilder keepAliveDurationMills(int keepAliveDurationInMills);

    UnderlyingHttpExecutorBuilder connectTimeoutMills(int connectTimeoutInMills);

    UnderlyingHttpExecutorBuilder requestTimeoutMills(int readTimeoutInMills);

    UnderlyingHttpExecutorBuilder proxy(Proxy proxy);

    UnderlyingHttpExecutorBuilder hostnameVerifier(HostnameVerifier hostnameVerifier);

    UnderlyingHttpExecutorBuilder sslContextBuilder(SSLContextBuilder sslContextBuilder);

    UnderlyingHttpExecutorBuilder executor(ExecutorService executor);

    @Override
    UnderlyingHttpExecutor build();
}
