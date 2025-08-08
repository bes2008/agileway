package com.jn.agileway.jetty;

import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpExecutor;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpExecutorBuilder;
import com.jn.langx.security.ssl.SSLContextBuilder;
import com.jn.langx.util.timing.TimeDuration;
import org.eclipse.jetty.client.HttpClient;

import javax.net.ssl.HostnameVerifier;
import java.net.Proxy;
import java.util.concurrent.ExecutorService;

public class JettyUnderlyingHttpExecutorBuilder implements UnderlyingHttpExecutorBuilder {
    private TimeDuration requestTimeout = TimeDuration.ofMinutes(2);
    private TimeDuration connectTimeout;
    private ExecutorService executor;
    private SSLContextBuilder sslContextBuilder;
    private Proxy proxy;

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
        if (connectTimeoutInMills > 0) {
            this.connectTimeout = TimeDuration.ofMillis(connectTimeoutInMills);
        }
        return this;
    }

    @Override
    public UnderlyingHttpExecutorBuilder requestTimeoutMills(int readTimeoutInMills) {
        this.requestTimeout = TimeDuration.ofMillis(readTimeoutInMills);
        return this;
    }

    @Override
    public UnderlyingHttpExecutorBuilder proxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    @Override
    public UnderlyingHttpExecutorBuilder hostnameVerifier(HostnameVerifier hostnameVerifier) {
        return null;
    }

    @Override
    public UnderlyingHttpExecutorBuilder sslContextBuilder(SSLContextBuilder sslContextBuilder) {
        this.sslContextBuilder = sslContextBuilder;
        return this;
    }

    @Override
    public UnderlyingHttpExecutorBuilder executor(ExecutorService executor) {
        this.executor = executor;
        return this;
    }

    @Override
    public UnderlyingHttpExecutor build() {
        HttpClient httpClient = new HttpClient();
        if (executor != null) {
            httpClient.setExecutor(executor);
        }
        if (proxy != null) {
            // httpClient.getProxyConfiguration().getProxies().add(proxy);
        }
        if (connectTimeout != null) {
            httpClient.setConnectTimeout(connectTimeout.toMillis());
        }
        httpClient.setMaxConnectionsPerDestination(1000);

        JettyUnderlyingHttpExecutor httpExecutor = new JettyUnderlyingHttpExecutor(httpClient, requestTimeout);
        return httpExecutor;
    }

    @Override
    public String getName() {
        return "jetty";
    }
}
