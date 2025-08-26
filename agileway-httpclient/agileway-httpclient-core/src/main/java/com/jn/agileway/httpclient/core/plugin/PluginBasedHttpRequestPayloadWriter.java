package com.jn.agileway.httpclient.core.plugin;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.payload.HttpRequestPayloadWriter;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;

import java.io.OutputStream;

public class PluginBasedHttpRequestPayloadWriter implements HttpRequestPayloadWriter {

    private HttpMessageProtocolPlugin plugin;

    public PluginBasedHttpRequestPayloadWriter(HttpMessageProtocolPlugin plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean canWrite(HttpRequest<?> request) {
        if (plugin.getRequestPayloadWriters().isEmpty()) {
            return false;
        }
        if (!plugin.availableFor(request)) {
            return false;
        }
        return Pipeline.of(plugin.getRequestPayloadWriters())
                .anyMatch(new Predicate<HttpRequestPayloadWriter>() {
                    @Override
                    public boolean test(HttpRequestPayloadWriter writer) {
                        return writer.canWrite(request);
                    }
                });
    }

    @Override
    public void write(HttpRequest<?> request, OutputStream output) throws Exception {
        Pipeline.of(plugin.getRequestPayloadWriters()).findFirst(new Predicate<HttpRequestPayloadWriter>() {
            @Override
            public boolean test(HttpRequestPayloadWriter httpRequestContentWriter) {
                return httpRequestContentWriter.canWrite(request);
            }
        }).write(request, output);

    }
}
