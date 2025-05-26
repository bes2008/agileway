package com.jn.agileway.httpclient.core.plugin;

import com.jn.agileway.httpclient.core.content.HttpResponseContentReader;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.langx.util.net.mime.MediaType;

import java.io.IOException;
import java.lang.reflect.Type;

public class GeneralPluginBasedHttpResponseReader implements HttpResponseContentReader {

    private HttpMessagePlugin plugin;

    public GeneralPluginBasedHttpResponseReader(HttpMessagePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean canRead(UnderlyingHttpResponse response, MediaType contentType, Type expectedContentType) {
        if (plugin.getRequestContentWriters().isEmpty()) {
            return false;
        }
        if (!plugin.availableFor(response)) {
            return false;
        }
        return true;
    }

    @Override
    public Object read(UnderlyingHttpResponse response, MediaType contentType, Type expectedContentType) throws IOException {
        return null;
    }
}
