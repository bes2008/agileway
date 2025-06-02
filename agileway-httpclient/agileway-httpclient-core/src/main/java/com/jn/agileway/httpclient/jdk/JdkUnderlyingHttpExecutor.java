package com.jn.agileway.httpclient.jdk;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.error.exception.UnsupportedHttpMethodException;
import com.jn.agileway.httpclient.core.payload.multipart.MultiPartsForm;
import com.jn.agileway.httpclient.core.underlying.AbstractUnderlyingHttpExecutor;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.agileway.httpclient.util.ContentEncoding;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.util.List;


public class JdkUnderlyingHttpExecutor extends AbstractUnderlyingHttpExecutor<HttpURLConnection> {
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


    protected void setHeaderToUnderlying(HttpURLConnection underlyingRequest, String headerName, String headerValue) {
        underlyingRequest.setRequestProperty(headerName, headerValue);
    }

    @Override
    protected void addHeaderToUnderlying(HttpURLConnection underlyingRequest, String headerName, String headerValue) {
        underlyingRequest.addRequestProperty(headerName, headerValue);
    }

    @Override
    public UnderlyingHttpResponse executeBufferedRequest(HttpRequest<byte[]> request) throws Exception {
        URI uri = request.getUri();
        HttpMethod method = request.getMethod();
        HttpHeaders httpHeaders = request.getHttpHeaders();
        boolean streamMode = HttpClientUtils.requestBodyUseStreamMode(method, httpHeaders);
        HttpURLConnection httpURLConnection = createHttpUrlConnection(method, uri);
        return exchangeInternal(request, httpURLConnection, streamMode);
    }

    @Override
    public UnderlyingHttpResponse executeAttachmentUploadRequest(HttpRequest<MultiPartsForm> request) throws Exception {
        return null;
    }

    protected UnderlyingHttpResponse exchangeInternal(HttpRequest<byte[]> request, HttpURLConnection httpConnection, boolean streamMode) throws IOException {
        if (!streamMode) {
            // buffered 模式
            writeHeaders(request, httpConnection);
            httpConnection.connect();
            if (httpConnection.getDoOutput() && request.getPayload() != null) {
                OutputStream outputStream = httpConnection.getOutputStream();
                List<ContentEncoding> contentEncodings = HttpClientUtils.getContentEncodings(request.getHttpHeaders());

                outputStream = HttpClientUtils.wrapByContentEncodings(outputStream, contentEncodings);
                outputStream.write(request.getPayload());
                outputStream.flush();
            }
        } else {
            try {
                long contentLength = request.getHttpHeaders().getContentLength();
                if (contentLength > 0) {
                    // 要求 提前告知长度
                    httpConnection.setFixedLengthStreamingMode(contentLength);
                } else {
                    httpConnection.setChunkedStreamingMode(4096);
                }
                writeHeaders(request, httpConnection);
                httpConnection.connect();
                OutputStream outputStream = httpConnection.getOutputStream();
                List<ContentEncoding> contentEncodings = HttpClientUtils.getContentEncodings(request.getHttpHeaders());
                outputStream = HttpClientUtils.wrapByContentEncodings(outputStream, contentEncodings);
                outputStream.write(request.getPayload());
                outputStream.flush();
            } catch (IOException ex) {
                throw Throwables.wrapAsRuntimeIOException(ex);
            }
        }

        httpConnection.getResponseCode();
        return new JdkUnderlyingHttpResponse(httpConnection);
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
        httpConn.setDoOutput(HttpClientUtils.isWriteableMethod(method));

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
