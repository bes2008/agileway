package com.jn.agileway.httpclient.core.plugin;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.payload.HttpRequestPayloadWriter;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;

import java.io.ByteArrayOutputStream;

public class PluginBasedHttpRequestWriter implements HttpRequestPayloadWriter {

    private HttpMessageProtocolPlugin plugin;

    public PluginBasedHttpRequestWriter(HttpMessageProtocolPlugin plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean canWrite(HttpRequest<?> request) {
        if (plugin.getRequestContentWriters().isEmpty()) {
            return false;
        }
        if (!plugin.availableFor(request)) {
            return false;
        }
        return Pipeline.of(plugin.getRequestContentWriters())
                .anyMatch(new Predicate<HttpRequestPayloadWriter>() {
                    @Override
                    public boolean test(HttpRequestPayloadWriter writer) {
                        return writer.canWrite(request);
                    }
                });
    }

    @Override
    public void write(HttpRequest<?> request, ByteArrayOutputStream output) throws Exception {
        Pipeline.of(plugin.getRequestContentWriters()).findFirst(new Predicate<HttpRequestPayloadWriter>() {
            @Override
            public boolean test(HttpRequestPayloadWriter httpRequestContentWriter) {
                return httpRequestContentWriter.canWrite(request);
            }
        }).write(request, output);

    }
}
