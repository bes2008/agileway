package com.jn.agileway.httpclient.core.serialize;

import com.jn.agileway.httpclient.core.HttpRequestBodyWriter;
import com.jn.agileway.httpclient.core.UnderlyingHttpRequest;
import com.jn.easyjson.core.util.JSONs;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.net.mime.MediaType;

import java.io.IOException;

public class GeneralJsonHttpRequestWriter implements HttpRequestBodyWriter {
    @Override
    public boolean canWrite(Object body, MediaType contentType) {
        if (MediaType.APPLICATION_JSON.equalsTypeAndSubtype(contentType)) {
            return true;
        }
        return false;
    }

    @Override
    public void write(Object body, MediaType contentType, UnderlyingHttpRequest output) throws IOException {
        output.getBody().write(JSONs.toJson(body).getBytes(Charsets.UTF_8));
    }
}
