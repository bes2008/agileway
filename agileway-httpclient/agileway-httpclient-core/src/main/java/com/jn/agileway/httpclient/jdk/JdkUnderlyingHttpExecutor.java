package com.jn.agileway.httpclient.jdk;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.MessageHeaderConstants;
import com.jn.agileway.httpclient.core.error.exception.UnsupportedHttpMethodException;
import com.jn.agileway.httpclient.core.payload.HttpRequestPayloadWriter;
import com.jn.agileway.httpclient.core.underlying.AbstractUnderlyingHttpExecutor;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.agileway.httpclient.util.ContentEncoding;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.timing.TimeDuration;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.List;


public class JdkUnderlyingHttpExecutor extends AbstractUnderlyingHttpExecutor<HttpURLConnection> {
    private Proxy proxy;
    private HostnameVerifier hostnameVerifier;
    /**
     * 创建http连接的超时时间
     */
    private int connectTimeoutMills;
    /**
     * 从http连接中读取数据的超时时间
     */
    private TimeDuration timeout = TimeDuration.ofMinutes(2);

    private SSLContext sslContext;


    public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
    }

    public void setConnectTimeoutMills(int connectTimeoutMills) {
        this.connectTimeoutMills = connectTimeoutMills;
    }

    public void setReadTimeoutMills(int readTimeoutMills) {
        this.timeout = TimeDuration.ofMillis(readTimeoutMills);
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
    public UnderlyingHttpResponse executeBufferedRequest(HttpRequest<ByteArrayOutputStream> request) throws Exception {
        URI uri = request.getUri();
        HttpMethod method = request.getMethod();
        HttpURLConnection httpConnection = createHttpUrlConnection(method, uri);

        TimeDuration requestTimeout = request.getHeaders().get(MessageHeaderConstants.REQUEST_KEY_TIMEOUT, TimeDuration.class);
        if (requestTimeout == null) {
            requestTimeout = this.timeout;
        }
        if (requestTimeout != null) {
            httpConnection.setReadTimeout((int) requestTimeout.toMillis());
        }

        // buffered 模式
        completeHeaders(request, httpConnection);
        httpConnection.connect();
        if (httpConnection.getDoOutput() && request.getPayload() != null) {
            OutputStream outputStream = httpConnection.getOutputStream();
            List<ContentEncoding> contentEncodings = HttpClientUtils.getContentEncodings(request.getHttpHeaders());

            outputStream = HttpClientUtils.wrapByContentEncodings(outputStream, contentEncodings);
            outputStream.write(request.getPayload().toByteArray());
            outputStream.flush();
        }
        httpConnection.getResponseCode();
        return new JdkUnderlyingHttpResponse(httpConnection);
    }

    @Override
    public UnderlyingHttpResponse executeAttachmentUploadRequest(HttpRequest<?> request, HttpRequestPayloadWriter payloadWriter) throws Exception {
        URI uri = request.getUri();
        HttpMethod method = request.getMethod();
        HttpURLConnection httpConnection = createHttpUrlConnection(method, uri);
        httpConnection.setChunkedStreamingMode(4096);
        completeHeaders(request, httpConnection);


        TimeDuration requestTimeout = request.getHeaders().get(MessageHeaderConstants.REQUEST_KEY_TIMEOUT, TimeDuration.class);
        if (requestTimeout == null) {
            requestTimeout = this.timeout;
        }
        if (requestTimeout != null) {
            httpConnection.setReadTimeout((int) requestTimeout.toMillis());
        }


        httpConnection.connect();
        OutputStream outputStream = httpConnection.getOutputStream();

        List<ContentEncoding> contentEncodings = HttpClientUtils.getContentEncodings(request.getHttpHeaders());
        outputStream = HttpClientUtils.wrapByContentEncodings(outputStream, contentEncodings);
        payloadWriter.write(request, outputStream);
        outputStream.flush();

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
        // 需要注意的是， JDK http client ，不支持 PATCH方法
        httpConn.setRequestMethod(method.name());

        httpConn.setDoInput(true);
        httpConn.setInstanceFollowRedirects(method == HttpMethod.GET);
        httpConn.setDoOutput(HttpClientUtils.isSupportContentMethod(method));

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
