package com.jn.agileway.httpclient.core.underlying;

import com.jn.agileway.httpclient.core.HttpProtocolVersion;
import com.jn.langx.Builder;
import com.jn.langx.Named;
import com.jn.langx.security.ssl.SSLContextBuilder;
import com.jn.langx.util.function.Supplier0;

import javax.net.ssl.HostnameVerifier;
import java.net.Proxy;
import java.util.List;
import java.util.concurrent.ExecutorService;

public interface UnderlyingHttpExecutorBuilder extends Builder<UnderlyingHttpExecutor>, Named, Supplier0<UnderlyingHttpExecutorBuilder> {

    UnderlyingHttpExecutorBuilder protocolVersion(HttpProtocolVersion protocolVersion);

    UnderlyingHttpExecutorBuilder poolMaxIdleConnections(int maxIdleConnections);

    UnderlyingHttpExecutorBuilder keepAliveDurationMills(int keepAliveDurationInMills);

    UnderlyingHttpExecutorBuilder connectTimeoutMills(int connectTimeoutInMills);

    UnderlyingHttpExecutorBuilder requestTimeoutMills(int readTimeoutInMills);

    UnderlyingHttpExecutorBuilder proxy(Proxy proxy);

    UnderlyingHttpExecutorBuilder hostnameVerifier(HostnameVerifier hostnameVerifier);

    UnderlyingHttpExecutorBuilder sslContextBuilder(SSLContextBuilder sslContextBuilder);

    UnderlyingHttpExecutorBuilder executor(ExecutorService executor);

    List<HttpProtocolVersion> supportedProtocols();

    /**
     * 例如 8 表示 jdk8， 17 表示 jdk17
     *
     * @return
     */
    int minJdkVersion();

    @Override
    UnderlyingHttpExecutor build();
}
