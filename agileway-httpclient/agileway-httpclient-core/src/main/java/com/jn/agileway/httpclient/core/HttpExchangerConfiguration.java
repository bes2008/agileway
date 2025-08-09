package com.jn.agileway.httpclient.core;

import com.jn.agileway.httpclient.core.ssl.DefaultHostnameVerifier;
import com.jn.langx.security.ssl.SSLContextBuilder;
import com.jn.langx.util.collection.multivalue.MultiValueMap;
import com.jn.langx.util.concurrent.CommonThreadFactory;

import javax.net.ssl.HostnameVerifier;
import java.net.Proxy;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HttpExchangerConfiguration {
    /***********************************************************************
     * HttpExchanger interceptor 所需的相关配置
     ***********************************************************************/
    private HttpProtocolVersion protocolVersion = HttpProtocolVersion.HTTP_1_1;

    /**
     * 对 HTTP(s) scheme 的限制
     */
    private List<String> allowedSchemes;
    /**
     * 对 HTTP(s) host,port 的限制
     */
    private List<String> allowedAuthorities;
    private List<String> notAllowedAuthorities;
    /**
     * 对 HTTP(s) method 的限制
     */
    private List<String> allowedMethods;
    private List<String> notAllowedMethods;
    /**
     * 固定 header
     */
    private MultiValueMap<String, String> fixedHeaders;

    /***********************************************************************
     * UnderlyingHttpRequestFactoryBuilder 所需的相关配置
     ***********************************************************************/

    /**
     * 连接超时时间
     */
    private int connectTimeoutMillis = 5000;
    /**
     * 读超时时间
     */
    private int readTimeoutMillis = 60000;
    private int keepAliveDurationMills = 60000;

    private int poolMaxIdleConnections = 5;

    private HostnameVerifier hostnameVerifier;

    private SSLContextBuilder sslContextBuilder;

    private Proxy proxy;

    private ExecutorService executor;

    public int getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public void setConnectTimeoutMillis(int connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    public int getReadTimeoutMillis() {
        return readTimeoutMillis;
    }

    public void setReadTimeoutMillis(int readTimeoutMillis) {
        this.readTimeoutMillis = readTimeoutMillis;
    }

    public int getKeepAliveDurationMills() {
        return keepAliveDurationMills;
    }

    public void setKeepAliveDurationMills(int keepAliveDurationMills) {
        this.keepAliveDurationMills = keepAliveDurationMills;
    }

    public int getPoolMaxIdleConnections() {
        return poolMaxIdleConnections;
    }

    public void setPoolMaxIdleConnections(int poolMaxIdleConnections) {
        this.poolMaxIdleConnections = poolMaxIdleConnections;
    }

    public HostnameVerifier getHostnameVerifier() {
        if (hostnameVerifier == null) {
            this.hostnameVerifier = new DefaultHostnameVerifier(allowedAuthorities, notAllowedAuthorities, proxy);
        }
        return hostnameVerifier;
    }

    public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
    }

    public SSLContextBuilder getSslContextBuilder() {
        return sslContextBuilder;
    }

    public void setSslContextBuilder(SSLContextBuilder sslContextBuilder) {
        this.sslContextBuilder = sslContextBuilder;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    public ExecutorService getExecutor() {
        if (executor == null) {
            executor = new ThreadPoolExecutor(5, 10, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(Integer.MAX_VALUE), new CommonThreadFactory("http-exchange", true));
        }
        return executor;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public List<String> getAllowedSchemes() {
        return allowedSchemes;
    }

    public void setAllowedSchemes(List<String> allowedSchemes) {
        this.allowedSchemes = allowedSchemes;
    }

    public List<String> getAllowedAuthorities() {
        return allowedAuthorities;
    }

    public void setAllowedAuthorities(List<String> allowedAuthorities) {
        this.allowedAuthorities = allowedAuthorities;
    }

    public List<String> getNotAllowedAuthorities() {
        return notAllowedAuthorities;
    }

    public void setNotAllowedAuthorities(List<String> notAllowedAuthorities) {
        this.notAllowedAuthorities = notAllowedAuthorities;
    }

    public List<String> getAllowedMethods() {
        return allowedMethods;
    }

    public void setAllowedMethods(List<String> allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    public List<String> getNotAllowedMethods() {
        return notAllowedMethods;
    }

    public void setNotAllowedMethods(List<String> notAllowedMethods) {
        this.notAllowedMethods = notAllowedMethods;
    }

    public void setFixedHeaders(MultiValueMap<String, String> fixedHeaders) {
        this.fixedHeaders = fixedHeaders;
    }

    public MultiValueMap<String, String> getFixedHeaders() {
        return fixedHeaders;
    }

    public HttpProtocolVersion getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(HttpProtocolVersion protocolVersion) {
        this.protocolVersion = protocolVersion;
    }
}
