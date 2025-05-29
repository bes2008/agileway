package com.jn.agileway.httpclient.core.plugin;

import com.jn.agileway.httpclient.core.HttpMessage;
import com.jn.agileway.httpclient.core.content.HttpRequestContentWriter;
import com.jn.agileway.httpclient.core.content.HttpResponseContentReader;
import com.jn.agileway.httpclient.core.interceptor.HttpRequestInterceptor;
import com.jn.agileway.httpclient.core.interceptor.HttpResponseInterceptor;
import com.jn.langx.lifecycle.AbstractInitializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.plugin.Plugin;
import com.jn.langx.util.collection.Lists;

import java.util.List;

public abstract class HttpMessageProtocolPlugin extends AbstractInitializable implements Plugin<HttpMessage> {
    protected final List<HttpRequestInterceptor> requestInterceptors = Lists.newArrayList();

    protected final List<HttpResponseInterceptor> responseInterceptors = Lists.newArrayList();

    protected final List<HttpRequestContentWriter> requestContentWriters = Lists.newArrayList();
    protected final List<HttpResponseContentReader> responseContentReaders = Lists.newArrayList();

    public List<HttpRequestContentWriter> getRequestContentWriters() {
        return requestContentWriters;
    }

    public List<HttpRequestInterceptor> getRequestInterceptors() {
        return requestInterceptors;
    }

    public List<HttpResponseInterceptor> getResponseInterceptors() {
        return responseInterceptors;
    }

    public List<HttpResponseContentReader> getResponseContentReaders() {
        return responseContentReaders;
    }

    @Override
    protected void doInit() throws InitializationException {
        initInternal();
    }

    protected abstract void initInternal();
}
