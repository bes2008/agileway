package com.jn.agileway.httpclient.jdk;

import com.jn.agileway.httpclient.core.AbstractUnderlyingHttpRequestFactory;
import com.jn.agileway.httpclient.core.UnderlyingHttpRequest;
import com.jn.agileway.httpclient.core.exception.UnsupportedHttpMethodException;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.os.Platform;

import javax.net.ssl.HttpsURLConnection;
import java.net.*;

public class JdkHttpRequestFactory extends AbstractUnderlyingHttpRequestFactory {
    private Proxy proxy;

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public UnderlyingHttpRequest create(HttpMethod method, URI uri, HttpHeaders httpHeaders) throws Exception {
        boolean streamMode = HttpClientUtils.requestBodyUseStreamMode(method, httpHeaders);
        return new JdkHttpRequest(createHttpUrlConnection(method, uri), streamMode);
    }


    private HttpURLConnection createHttpUrlConnection(HttpMethod method, URI uri) throws Exception {
        if (method == HttpMethod.PATCH) {
            throw new UnsupportedHttpMethodException("The JDK HttpURLConnection does not support PATCH method");
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
            httpsConn.setSSLSocketFactory(getSslSocketFactory());
        }

        return httpConn;
    }
}
