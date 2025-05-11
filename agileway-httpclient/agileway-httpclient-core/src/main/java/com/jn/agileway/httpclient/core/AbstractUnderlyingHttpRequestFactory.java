package com.jn.agileway.httpclient.core;

import javax.net.ssl.SSLSocketFactory;

public abstract class AbstractUnderlyingHttpRequestFactory implements UnderlyingHttpRequestFactory {

    protected int connectTimeoutMills;
    protected int readTimeoutMills;
    private SSLSocketFactory sslSocketFactory;

    @Override
    public void setConnectTimeoutMills(int connectTimeoutMills) {
        this.connectTimeoutMills = connectTimeoutMills;
    }

    @Override
    public void setReadTimeoutMills(int readTimeoutMills) {
        this.readTimeoutMills = readTimeoutMills;
    }

    public void setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
    }

    protected SSLSocketFactory getSslSocketFactory() {
        return this.sslSocketFactory == null ? (SSLSocketFactory) SSLSocketFactory.getDefault() : this.sslSocketFactory;
    }
}
