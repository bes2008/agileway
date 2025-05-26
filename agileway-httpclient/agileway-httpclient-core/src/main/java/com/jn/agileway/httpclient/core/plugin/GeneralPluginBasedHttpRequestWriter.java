package com.jn.agileway.httpclient.core.plugin;

import com.jn.agileway.httpclient.core.content.HttpRequestContentWriter;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpRequest;
import com.jn.langx.plugin.PluginRegistry;
import com.jn.langx.util.net.mime.MediaType;

import java.io.IOException;

public class GeneralPluginBasedHttpRequestWriter implements HttpRequestContentWriter {

    private PluginRegistry httpMessagePluginRegistry;

    public GeneralPluginBasedHttpRequestWriter(PluginRegistry httpMessagePluginRegistry) {
        this.httpMessagePluginRegistry = httpMessagePluginRegistry;
    }


    @Override
    public boolean canWrite(Object body, MediaType contentType) {
        return false;
    }

    @Override
    public void write(Object body, MediaType contentType, UnderlyingHttpRequest output) throws IOException {

    }
}
