package com.jn.agileway.jetty;

import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpExecutor;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpExecutorBuilder;
import com.jn.langx.security.ssl.SSLContextBuilder;
import org.eclipse.jetty.client.HttpClient;

import javax.net.ssl.HostnameVerifier;
import java.net.Proxy;
import java.util.concurrent.ExecutorService;

public class JettyUnderlyingHttpExecutorBuilder implements UnderlyingHttpExecutorBuilder {
    @Override
    public UnderlyingHttpExecutorBuilder poolMaxIdleConnections(int maxIdleConnections) {
        return null;
    }

    @Override
    public UnderlyingHttpExecutorBuilder keepAliveDurationMills(int keepAliveDurationInMills) {
        return null;
    }

    @Override
    public UnderlyingHttpExecutorBuilder connectTimeoutMills(int connectTimeoutInMills) {
        return null;
    }

    @Override
    public UnderlyingHttpExecutorBuilder requestTimeoutMills(int readTimeoutInMills) {
        return null;
    }

    @Override
    public UnderlyingHttpExecutorBuilder proxy(Proxy proxy) {
        return null;
    }

    @Override
    public UnderlyingHttpExecutorBuilder hostnameVerifier(HostnameVerifier hostnameVerifier) {
        return null;
    }

    @Override
    public UnderlyingHttpExecutorBuilder sslContextBuilder(SSLContextBuilder sslContextBuilder) {
        return null;
    }

    @Override
    public UnderlyingHttpExecutorBuilder executor(ExecutorService executor) {
        return null;
    }

    @Override
    public UnderlyingHttpExecutor build() {
        HttpClient httpClient = new HttpClient();

        JettyUnderlyingHttpExecutor httpExecutor = new JettyUnderlyingHttpExecutor(httpClient);
        return httpExecutor;
    }

    @Override
    public String getName() {
        return "jetty";
    }
}
