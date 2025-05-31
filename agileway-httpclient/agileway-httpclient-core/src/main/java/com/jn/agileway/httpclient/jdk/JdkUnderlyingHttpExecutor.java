package com.jn.agileway.httpclient.jdk;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.httpclient.core.error.exception.UnsupportedHttpMethodException;
import com.jn.agileway.httpclient.core.underlying.AbstractUnderlyingHttpExecutor;
import com.jn.agileway.httpclient.util.ContentEncoding;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.util.Strings;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    public HttpResponse<InputStream> execute(HttpRequest<ByteArrayOutputStream> request) throws Exception {
        URI uri = request.getUri();
        HttpMethod method = request.getMethod();
        HttpHeaders httpHeaders = request.getHttpHeaders();
        boolean streamMode = HttpClientUtils.requestBodyUseStreamMode(method, httpHeaders);
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = createHttpUrlConnection(method, uri);
            return exchangeInternal(request, httpURLConnection, streamMode);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }

    protected HttpResponse<InputStream> exchangeInternal(HttpRequest<ByteArrayOutputStream> request, HttpURLConnection httpConnection, boolean streamMode) throws IOException {
        if (!streamMode) {
            // buffered 模式
            writeHeaders(request, httpConnection);
            httpConnection.connect();
            if (httpConnection.getDoOutput() && request.getPayload() != null) {
                OutputStream outputStream = httpConnection.getOutputStream();
                List<ContentEncoding> contentEncodings = HttpClientUtils.getContentEncodings(request.getHttpHeaders());

                outputStream = HttpClientUtils.wrapByContentEncodings(outputStream, contentEncodings);
                outputStream.write(request.getPayload().toByteArray());
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
                outputStream.write(request.getPayload().toByteArray());
                outputStream.flush();
            } catch (IOException ex) {
                throw Throwables.wrapAsRuntimeIOException(ex);
            }
        }

        int statusCode = httpConnection.getResponseCode();

        HttpHeaders httpHeaders = extractResponseHeader(httpConnection);
        InputStream inputStream = extractResponseBody(httpConnection, httpHeaders);
        HttpResponse<InputStream> response = new HttpResponse<InputStream>(
                request.getMethod(),
                request.getUri(),
                statusCode,
                httpHeaders,
                null,
                inputStream
        );
        return response;
    }

    private InputStream extractResponseBody(HttpURLConnection httpConnection, HttpHeaders responseHeaders) throws IOException {
        InputStream inputStream = httpConnection.getErrorStream();
        if (inputStream == null) {
            inputStream = httpConnection.getInputStream();
        }

        // 处理压缩
        List<ContentEncoding> contentEncodings = HttpClientUtils.getContentEncodings(responseHeaders);
        inputStream = HttpClientUtils.wrapByContentEncodings(inputStream, contentEncodings);
        return inputStream;
    }


    private HttpHeaders extractResponseHeader(HttpURLConnection httpConnection) {
        HttpHeaders headers = new HttpHeaders();
        // Header field 0 is the status line for most HttpURLConnections, but not on GAE
        String name = httpConnection.getHeaderFieldKey(0);
        if (Strings.isNotBlank(name)) {
            headers.add(name, httpConnection.getHeaderField(0));
        }
        int i = 1;
        while (true) {
            name = httpConnection.getHeaderFieldKey(i);
            if (Strings.isBlank(name)) {
                break;
            }
            headers.add(name, httpConnection.getHeaderField(i));
            i++;
        }
        return headers;
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
