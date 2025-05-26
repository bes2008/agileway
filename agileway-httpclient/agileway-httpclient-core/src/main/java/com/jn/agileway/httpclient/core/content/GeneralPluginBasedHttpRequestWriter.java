package com.jn.agileway.httpclient.core.content;

import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpRequest;
import com.jn.langx.util.net.mime.MediaType;

import java.io.IOException;

public class GeneralPluginBasedHttpRequestWriter implements HttpRequestContentWriter {
    @Override
    public boolean canWrite(Object body, MediaType contentType) {
        return false;
    }

    @Override
    public void write(Object body, MediaType contentType, UnderlyingHttpRequest output) throws IOException {

    }
}
