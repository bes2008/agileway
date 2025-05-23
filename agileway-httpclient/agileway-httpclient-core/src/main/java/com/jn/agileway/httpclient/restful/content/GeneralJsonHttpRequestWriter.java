package com.jn.agileway.httpclient.restful.content;

import com.jn.agileway.httpclient.core.content.HttpRequestContentWriter;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpRequest;
import com.jn.easyjson.core.util.JSONs;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.net.mime.MediaType;

import java.io.IOException;

public class GeneralJsonHttpRequestWriter implements HttpRequestContentWriter {
    @Override
    public boolean canWrite(Object body, MediaType contentType) {
        if (MediaType.APPLICATION_JSON.equalsTypeAndSubtype(contentType)) {
            return true;
        }
        return false;
    }

    @Override
    public void write(Object body, MediaType contentType, UnderlyingHttpRequest output) throws IOException {
        output.getBufferedContent().write(JSONs.toJson(body).getBytes(Charsets.UTF_8));
    }
}
