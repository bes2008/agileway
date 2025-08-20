package com.jn.agileway.httpclient.jetty;

import com.jn.agileway.httpclient.core.HttpProtocolVersion;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpExecutor;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpExecutorBuilder;
import com.jn.langx.security.ssl.SSLContextBuilder;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.timing.TimeDuration;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;

import javax.net.ssl.HostnameVerifier;
import java.net.Proxy;
import java.util.List;

public class JettyUnderlyingHttpExecutorBuilder implements UnderlyingHttpExecutorBuilder {
    private TimeDuration requestTimeout = TimeDuration.ofMinutes(2);
    private TimeDuration connectTimeout;
    private int workerThreads;
    private SSLContextBuilder sslContextBuilder;
    private Proxy proxy;

    private HttpProtocolVersion protocolVersion;

    @Override
    public UnderlyingHttpExecutorBuilder protocolVersion(HttpProtocolVersion protocolVersion) {
        this.protocolVersion = protocolVersion;
        return this;
    }

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
    public UnderlyingHttpExecutorBuilder workThreads(int workerThreads) {
        this.workerThreads = workerThreads;
        return this;
    }


    private static final List<HttpProtocolVersion> supportedProtocols = Lists.newArrayList(
            HttpProtocolVersion.HTTP_1_1,
            HttpProtocolVersion.HTTP_2
    );

    @Override
    public List<HttpProtocolVersion> supportedProtocols() {
        return supportedProtocols;
    }

    @Override
    public int minJdkVersion() {
        return 17;
    }


    @Override
    public UnderlyingHttpExecutor build() {
        HttpClient httpClient = new HttpClient();
        if (workerThreads > 0) {
            httpClient.setExecutor(workerThreads < 8 ? new ExecutorThreadPool(workerThreads, workerThreads) : new ExecutorThreadPool(workerThreads));
        }
        if (proxy != null) {
            // httpClient.getProxyConfiguration().getProxies().add(proxy);
        }
        if (connectTimeout != null) {
            httpClient.setConnectTimeout(connectTimeout.toMillis());
        }
        httpClient.setMaxConnectionsPerDestination(1000);

        JettyUnderlyingHttpExecutor httpExecutor = new JettyUnderlyingHttpExecutor(httpClient, requestTimeout);
        try {
            httpClient.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return httpExecutor;
    }

    @Override
    public String getName() {
        return "jetty";
    }

    public UnderlyingHttpExecutorBuilder get() {
        return new JettyUnderlyingHttpExecutorBuilder();
    }
}
