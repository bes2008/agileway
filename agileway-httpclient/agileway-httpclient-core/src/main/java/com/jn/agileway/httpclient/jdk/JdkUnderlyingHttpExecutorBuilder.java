package com.jn.agileway.httpclient.jdk;

import com.jn.agileway.httpclient.core.HttpProtocolVersion;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpExecutor;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpExecutorBuilder;
import com.jn.langx.security.ssl.SSLContextBuilder;
import com.jn.langx.util.collection.Lists;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.net.Proxy;
import java.util.List;

public class JdkUnderlyingHttpExecutorBuilder implements UnderlyingHttpExecutorBuilder {

    private int connectTimeoutInMills = 3000;
    private int readTimeoutInMills = 60000;
    private Proxy proxy;
    private HostnameVerifier hostnameVerifier;
    private SSLContextBuilder sslContextBuilder;

    @Override
    public UnderlyingHttpExecutorBuilder protocolVersion(HttpProtocolVersion protocolVersion) {
        return this;
    }

    @Override
    public String getName() {
        return "jdk";
    }

    @Override
    public JdkUnderlyingHttpExecutorBuilder poolMaxIdleConnections(int maxIdleConnections) {
        return this;
    }

    @Override
    public JdkUnderlyingHttpExecutorBuilder keepAliveDurationMills(int keepAliveDurationInMills) {
        return this;
    }

    @Override
    public JdkUnderlyingHttpExecutorBuilder connectTimeoutMills(int connectTimeoutInMills) {
        this.connectTimeoutInMills = connectTimeoutInMills;
        return this;
    }

    @Override
    public JdkUnderlyingHttpExecutorBuilder requestTimeoutMills(int readTimeoutInMills) {
        this.readTimeoutInMills = readTimeoutInMills;
        return this;
    }

    @Override
    public JdkUnderlyingHttpExecutorBuilder proxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    @Override
    public JdkUnderlyingHttpExecutorBuilder hostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    @Override
    public JdkUnderlyingHttpExecutorBuilder sslContextBuilder(SSLContextBuilder sslContextBuilder) {
        this.sslContextBuilder = sslContextBuilder;
        return this;
    }

    @Override
    public JdkUnderlyingHttpExecutorBuilder workThreads(int workerThreads) {
        return this;
    }

    private static final List<HttpProtocolVersion> supportedProtocols = Lists.newArrayList(HttpProtocolVersion.HTTP_1_1);

    @Override
    public List<HttpProtocolVersion> supportedProtocols() {
        return supportedProtocols;
    }

    @Override
    public int minJdkVersion() {
        return 5;
    }

    @Override
    public UnderlyingHttpExecutor build() {
        JdkUnderlyingHttpExecutor httpExecutor = new JdkUnderlyingHttpExecutor();
        httpExecutor.setConnectTimeoutMills(connectTimeoutInMills);
        httpExecutor.setReadTimeoutMills(readTimeoutInMills);
        httpExecutor.setProxy(proxy);
        httpExecutor.setHostnameVerifier(hostnameVerifier);
        if (sslContextBuilder != null) {
            SSLContext sslContext = sslContextBuilder.build();
            httpExecutor.setSSLContext(sslContext);
        }
        return httpExecutor;
    }

    public UnderlyingHttpExecutorBuilder get() {
        return new JdkUnderlyingHttpExecutorBuilder();
    }
}
