package com.jn.agileway.httpclient.core.plugin;

import com.jn.agileway.httpclient.core.content.HttpResponseContentReader;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.langx.plugin.PluginRegistry;
import com.jn.langx.util.net.mime.MediaType;

import java.io.IOException;
import java.lang.reflect.Type;

public class GeneralPluginBasedHttpResponseReader implements HttpResponseContentReader {

    private PluginRegistry httpMessagePluginRegistry;

    public GeneralPluginBasedHttpResponseReader(PluginRegistry httpMessagePluginRegistry) {
        this.httpMessagePluginRegistry = httpMessagePluginRegistry;
    }

    @Override
    public boolean canRead(UnderlyingHttpResponse response, MediaType contentType, Type expectedContentType) {
        return false;
    }

    @Override
    public Object read(UnderlyingHttpResponse response, MediaType contentType, Type expectedContentType) throws IOException {
        return null;
    }
}
