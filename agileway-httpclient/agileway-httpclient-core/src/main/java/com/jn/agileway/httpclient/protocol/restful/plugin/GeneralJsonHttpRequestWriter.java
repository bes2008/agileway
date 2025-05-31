package com.jn.agileway.httpclient.protocol.restful.plugin;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.payload.HttpRequestPayloadWriter;
import com.jn.easyjson.core.util.JSONs;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.net.mime.MediaType;

import java.io.ByteArrayOutputStream;

class GeneralJsonHttpRequestWriter implements HttpRequestPayloadWriter {
    @Override
    public boolean canWrite(HttpRequest<?> request) {
        MediaType contentType = request.getHttpHeaders().getContentType();
        if (MediaType.APPLICATION_JSON.equalsTypeAndSubtype(contentType)) {
            return true;
        }
        return false;
    }

    @Override
    public void write(HttpRequest<?> request, ByteArrayOutputStream output) throws Exception {
        Object body = request.getPayload();
        output.write(JSONs.toJson(body).getBytes(Charsets.UTF_8));
    }
}
