package com.jn.agileway.httpclient.core.plugin;

import com.jn.agileway.httpclient.core.payload.HttpResponsePayloadReader;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.net.mime.MediaType;

import java.lang.reflect.Type;

public class PluginBasedHttpResponseReader implements HttpResponsePayloadReader {

    private HttpMessageProtocolPlugin plugin;

    public PluginBasedHttpResponseReader(HttpMessageProtocolPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean canRead(UnderlyingHttpResponse response, MediaType contentType, Type expectedContentType) {
        if (plugin.getResponseContentReaders().isEmpty()) {
            return false;
        }
        if (!plugin.availableFor(response)) {
            return false;
        }
        return Pipeline.of(plugin.getResponseContentReaders())
                .anyMatch(new Predicate<HttpResponsePayloadReader>() {
                    @Override
                    public boolean test(HttpResponsePayloadReader reader) {
                        return reader.canRead(response, contentType, expectedContentType);
                    }
                });
    }

    @Override
    public Object read(UnderlyingHttpResponse response, MediaType contentType, Type expectedContentType) throws Exception {
        return Pipeline.of(plugin.getResponseContentReaders())
                .findFirst(new Predicate<HttpResponsePayloadReader>() {
                    @Override
                    public boolean test(HttpResponsePayloadReader reader) {
                        return reader.canRead(response, contentType, expectedContentType);
                    }
                }).read(response, contentType, expectedContentType);
    }
}
