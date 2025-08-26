package com.jn.agileway.httpclient.core;

import com.jn.agileway.httpclient.core.ssl.DefaultHostnameVerifier;
import com.jn.langx.security.ssl.SSLContextBuilder;
import com.jn.langx.util.collection.multivalue.MultiValueMap;
import com.jn.langx.util.concurrent.CommonThreadFactory;

import javax.net.ssl.HostnameVerifier;
import java.net.Proxy;
import java.util.List;
import java.util.concurrent.*;

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
    private int requestTimeoutMillis = 60000;
    private int keepAliveDurationMills = 60000;

    private int poolMaxIdleConnections = 5;

    private HostnameVerifier hostnameVerifier;

    private SSLContextBuilder sslContextBuilder;

    private Proxy proxy;

    /**
     * 底层 http client 的工作线程数
     */
    private int workerThreads = 8;
    private ExecutorService executor;

    public int getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public void setConnectTimeoutMillis(int connectTimeoutMillis) {
        if (connectTimeoutMillis > 5000) {
            this.connectTimeoutMillis = connectTimeoutMillis;
        }
    }

    public int getRequestTimeoutMillis() {
        return requestTimeoutMillis;
    }

    public void setRequestTimeoutMillis(int requestTimeoutMillis) {
        this.requestTimeoutMillis = requestTimeoutMillis;
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

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    Executor getExecutor() {
        if (executor == null) {
            executor = new ThreadPoolExecutor(8, 16, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(10000), new CommonThreadFactory("http-exchanger", false));
        }
        return executor;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    public int getWorkerThreads() {
        return Math.max(workerThreads, 1);
    }

    public void setWorkerThreads(int workerThreads) {
        this.workerThreads = workerThreads;
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
