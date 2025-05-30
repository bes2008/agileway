package com.jn.agileway.httpclient.core;

import com.jn.agileway.eipchannel.core.message.BaseMessage;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;

import java.net.URI;

public abstract class BaseHttpMessage<T> extends BaseMessage<HttpHeaders, T> implements HttpMessage<T> {
    protected URI uri;
    protected HttpMethod method;

    /**
     * 自定义的期望的 payload 类型
     */
    public static final String HEADER_KEY_REPLY_PAYLOAD_TYPE = "agileway_http_reply_payload_type";

    public URI getUri() {
        return uri;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public HttpHeaders getHttpHeaders() {
        return this.getHeaders().getProtocolHeaders();
    }
}
