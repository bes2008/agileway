package com.jn.agileway.httpclient.jdk;

import com.jn.agileway.httpclient.core.UnderlyingHttpRequest;
import com.jn.agileway.httpclient.core.UnderlyingHttpRequestFactory;
import com.jn.agileway.httpclient.core.exception.UnsupportedHttpMethodException;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.net.*;
import java.util.concurrent.ExecutorService;

public class JdkUnderlyingHttpRequestFactory implements UnderlyingHttpRequestFactory {
    private Proxy proxy;
    private HostnameVerifier hostnameVerifier;
    /**
     * 创建http连接的超时时间
     */
    protected int connectTimeoutMills;
    /**
     * 从http连接中读取数据的超时时间
     */
    protected int readTimeoutMills;

    private SSLContext sslContext;


    public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
    }

    public void setConnectTimeoutMills(int connectTimeoutMills) {
        this.connectTimeoutMills = connectTimeoutMills;
    }

    public void setReadTimeoutMills(int readTimeoutMills) {
        this.readTimeoutMills = readTimeoutMills;
    }

    public void setSSLContext(SSLContext sslContext) {
        this.sslContext = sslContext;
    }


    protected SSLSocketFactory getSslSocketFactory() {
        return this.sslContext == null ? (SSLSocketFactory) SSLSocketFactory.getDefault() : this.sslContext.getSocketFactory();
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public UnderlyingHttpRequest create(HttpMethod method, URI uri, HttpHeaders httpHeaders) throws Exception {
        boolean streamMode = HttpClientUtils.requestBodyUseStreamMode(method, httpHeaders);
        return new JdkUnderlyingHttpRequest(method, uri, httpHeaders, createHttpUrlConnection(method, uri), streamMode);
    }


    private HttpURLConnection createHttpUrlConnection(HttpMethod method, URI uri) throws Exception {
        if (method == HttpMethod.PATCH) {
            throw new UnsupportedHttpMethodException(method, uri, "The JDK HttpURLConnection does not support PATCH method");
        }

        URL url = uri.toURL();
        URLConnection connection = proxy != null ? url.openConnection(proxy) : url.openConnection();
        HttpURLConnection httpConn = (HttpURLConnection) connection;
        if (this.connectTimeoutMills >= 0) {
            httpConn.setConnectTimeout(this.connectTimeoutMills);
        }
        if (this.readTimeoutMills >= 0) {
            httpConn.setReadTimeout(this.readTimeoutMills);
        }
        // 需要注意的是， JDK http client ，不支持 PATCH方法
        httpConn.setRequestMethod(method.name());

        httpConn.setDoInput(true);
        httpConn.setInstanceFollowRedirects(method == HttpMethod.GET);
        httpConn.setDoOutput(HttpClientUtils.isWriteable(method));

        if (HttpClientUtils.isSSLEnabled(uri)) {
            HttpsURLConnection httpsConn = (HttpsURLConnection) httpConn;
            if (this.hostnameVerifier != null) {
                httpsConn.setHostnameVerifier(hostnameVerifier);
            }
            httpsConn.setSSLSocketFactory(getSslSocketFactory());
        }

        return httpConn;
    }
}
