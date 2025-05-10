package com.jn.agileway.httpclient.jdk;

import com.jn.agileway.httpclient.core.UnderlyingHttpRequest;
import com.jn.agileway.httpclient.core.UnderlyingHttpRequestFactory;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.net.mime.MediaType;
import com.jn.langx.util.os.Platform;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.net.*;

public class JdkHttpRequestFactory implements UnderlyingHttpRequestFactory {
    private Proxy proxy;
    private int connectTimeoutMills = -1;
    private int readTimeoutMills = -1;

    private SSLSocketFactory sslSocketFactory;

    public void setSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
    }

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
            throw new UnsupportedOperationException("The JDK http client does not support PATCH method");
        }

        URL url = uri.toURL();
        URLConnection connection = proxy != null ? url.openConnection(proxy) : url.openConnection();
        if (!(connection instanceof HttpURLConnection)) {
            throw new UnsupportedOperationException("The url connection is not a http(s) connection");
        }
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
        httpConn.setDoOutput(writable(method));

        String scheme = uri.getScheme();
        if ("https".equals(scheme) || "wss".equals(scheme)) {
            HttpsURLConnection httpsConn = (HttpsURLConnection) httpConn;
            httpsConn.setSSLSocketFactory(sslSocketFactory);
        }

        return httpConn;
    }

    private boolean writable(HttpMethod method) {
        if (HttpMethod.POST == method || HttpMethod.PUT == method || HttpMethod.PATCH == method) {
            return true;
        }
        if (HttpMethod.DELETE == method) {
            return Platform.is8VMOrGreater();
        }
        return false;
    }
}
