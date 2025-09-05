package com.jn.agileway.httpclient.xml.plugin;

import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.httpclient.core.payload.HttpResponsePayloadReader;
import com.jn.langx.util.net.mime.MediaType;

import java.lang.reflect.Type;

public class XmlHttpResponseReader implements HttpResponsePayloadReader {

    @Override
    public boolean canRead(HttpResponse response, MediaType contentType, Type expectedContentType) {
        return false;
    }

    @Override
    public Object read(HttpResponse response, MediaType contentType, Type expectedContentType) throws Exception {
        return null;
    }
}
