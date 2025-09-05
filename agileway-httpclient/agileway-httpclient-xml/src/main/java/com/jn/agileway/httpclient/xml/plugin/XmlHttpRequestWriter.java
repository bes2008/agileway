package com.jn.agileway.httpclient.xml.plugin;

import com.jn.agileway.codec.serialization.xml.OXMs;
import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.payload.HttpRequestPayloadWriter;
import com.jn.langx.util.net.mime.MediaType;

import java.io.OutputStream;

public class XmlHttpRequestWriter implements HttpRequestPayloadWriter {

    @Override
    public boolean canWrite(HttpRequest<?> request) {
        MediaType contentType = request.getHttpHeaders().getContentType();
        if (contentType == null) {
            return false;
        }
        if (!MediaType.APPLICATION_XML.equalsTypeAndSubtype(contentType)) {
            return false;
        }
        return OXMs.isCanSerialize(request.getPayload().getClass());
    }

    @Override
    public void write(HttpRequest<?> request, OutputStream output) throws Exception {
        OXMs.marshal(request.getPayload(), output);
    }
}
