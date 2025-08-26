package com.jn.agileway.httpclient.core.plugin;

import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.httpclient.core.payload.HttpResponsePayloadReader;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.net.mime.MediaType;

import java.lang.reflect.Type;

public class PluginBasedHttpResponsePayloadReader implements HttpResponsePayloadReader<Object> {

    private HttpMessageProtocolPlugin plugin;

    public PluginBasedHttpResponsePayloadReader(HttpMessageProtocolPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean canRead(HttpResponse<byte[]> response, MediaType contentType, Type expectedContentType) {
        if (plugin.getResponsePayloadReaders().isEmpty()) {
            return false;
        }
        if (!plugin.availableFor(response)) {
            return false;
        }
        return Pipeline.of(plugin.getResponsePayloadReaders())
                .anyMatch(new Predicate<HttpResponsePayloadReader>() {
                    @Override
                    public boolean test(HttpResponsePayloadReader reader) {
                        return reader.canRead(response, contentType, expectedContentType);
                    }
                });
    }

    @Override
    public Object read(HttpResponse<byte[]> response, MediaType contentType, Type expectedContentType) throws Exception {
        return Pipeline.of(plugin.getResponsePayloadReaders())
                .findFirst(new Predicate<HttpResponsePayloadReader>() {
                    @Override
                    public boolean test(HttpResponsePayloadReader reader) {
                        return reader.canRead(response, contentType, expectedContentType);
                    }
                }).read(response, contentType, expectedContentType);
    }
}
