package com.jn.agileway.httpclient.protocol.restful.plugin;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.content.HttpRequestPayloadWriter;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpRequest;
import com.jn.easyjson.core.util.JSONs;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.net.mime.MediaType;

class GeneralJsonHttpRequestWriter implements HttpRequestPayloadWriter {
    @Override
    public boolean canWrite(HttpRequest request) {
        MediaType contentType = request.getHeaders().getContentType();
        if (MediaType.APPLICATION_JSON.equalsTypeAndSubtype(contentType)) {
            return true;
        }
        return false;
    }

    @Override
    public void write(HttpRequest request, UnderlyingHttpRequest output) throws Exception {
        Object body = request.getPayload();
        output.getPayload().write(JSONs.toJson(body).getBytes(Charsets.UTF_8));
    }
}
