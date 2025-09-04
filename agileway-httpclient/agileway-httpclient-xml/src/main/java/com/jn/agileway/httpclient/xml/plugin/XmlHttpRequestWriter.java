package com.jn.agileway.httpclient.xml.plugin;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.payload.HttpRequestPayloadWriter;

import java.io.OutputStream;

public class XmlHttpRequestWriter implements HttpRequestPayloadWriter {
    private XmlSerializationContext context;

    @Override
    public boolean canWrite(HttpRequest<?> request) {
        return false;
    }

    @Override
    public void write(HttpRequest<?> request, OutputStream output) throws Exception {

    }
}
