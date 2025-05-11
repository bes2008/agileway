package com.jn.agileway.httpclient.core;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.net.mime.MediaType;

import javax.net.ssl.SSLSocketFactory;
import java.net.URI;

public interface UnderlyingHttpRequestFactory {
    /**
     * 创建http连接的超时时间
     *
     * @param timeout
     */
    void setConnectTimeoutMills(int timeout);

    /**
     * 从http连接中读取数据的超时时间
     *
     * @param timeout
     */
    void setReadTimeoutMills(int timeout);

    void setSslSocketFactory(SSLSocketFactory sslSocketFactory);

    UnderlyingHttpRequest create(@NonNull HttpMethod method, @NonNull URI uri, @Nullable HttpHeaders httpHeaders) throws Exception;
}
