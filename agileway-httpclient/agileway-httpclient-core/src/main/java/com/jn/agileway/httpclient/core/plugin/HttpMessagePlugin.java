package com.jn.agileway.httpclient.core.plugin;

import com.jn.agileway.httpclient.core.HttpMessage;
import com.jn.agileway.httpclient.core.content.HttpRequestContentWriter;
import com.jn.agileway.httpclient.core.content.HttpResponseContentReader;
import com.jn.agileway.httpclient.core.interceptor.HttpRequestInterceptor;
import com.jn.langx.plugin.Plugin;
import com.jn.langx.util.collection.Lists;

import java.util.List;

public abstract class HttpMessagePlugin implements Plugin<HttpMessage> {
    private List<HttpRequestInterceptor> requestInterceptors = Lists.newArrayList();

    private List<HttpRequestInterceptor> responseInterceptors = Lists.newArrayList();

    private List<HttpRequestContentWriter> requestContentWriters = Lists.newArrayList();
    private List<HttpResponseContentReader> responseContentReaders = Lists.newArrayList();

    public List<HttpRequestContentWriter> getRequestContentWriters() {
        return requestContentWriters;
    }

    public List<HttpRequestInterceptor> getRequestInterceptors() {
        return requestInterceptors;
    }

    public List<HttpRequestInterceptor> getResponseInterceptors() {
        return responseInterceptors;
    }

    public List<HttpResponseContentReader> getResponseContentReaders() {
        return responseContentReaders;
    }
}
