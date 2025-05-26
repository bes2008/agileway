package com.jn.agileway.httpclient.core.plugin;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.content.HttpRequestContentWriter;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpRequest;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;

import java.io.IOException;

public class PluginBasedHttpRequestWriter implements HttpRequestContentWriter {

    private HttpMessagePlugin plugin;

    public PluginBasedHttpRequestWriter(HttpMessagePlugin plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean canWrite(HttpRequest request) {
        if (plugin.getRequestContentWriters().isEmpty()) {
            return false;
        }
        if (!plugin.availableFor(request)) {
            return false;
        }
        return Pipeline.of(plugin.getRequestContentWriters())
                .anyMatch(new Predicate<HttpRequestContentWriter>() {
                    @Override
                    public boolean test(HttpRequestContentWriter writer) {
                        return writer.canWrite(request);
                    }
                });
    }

    @Override
    public void write(HttpRequest request, UnderlyingHttpRequest output) throws IOException {
        Pipeline.of(plugin.getRequestContentWriters()).findFirst(new Predicate<HttpRequestContentWriter>() {
            @Override
            public boolean test(HttpRequestContentWriter httpRequestContentWriter) {
                return httpRequestContentWriter.canWrite(request);
            }
        }).write(request, output);

    }
}
