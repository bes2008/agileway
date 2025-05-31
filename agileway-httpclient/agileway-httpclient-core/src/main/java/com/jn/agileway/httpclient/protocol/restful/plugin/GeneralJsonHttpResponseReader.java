package com.jn.agileway.httpclient.protocol.restful.plugin;

import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.httpclient.core.payload.HttpResponsePayloadReader;
import com.jn.easyjson.core.util.JSONs;
import com.jn.langx.util.net.mime.MediaType;

import java.lang.reflect.Type;

class GeneralJsonHttpResponseReader implements HttpResponsePayloadReader<Object> {
    @Override
    public boolean canRead(HttpResponse<byte[]> response, MediaType contentType, Type expectedContentType) {
        if (MediaType.APPLICATION_JSON.equalsTypeAndSubtype(contentType)) {
            return true;
        }
        return false;
    }

    @Override
    public Object read(HttpResponse<byte[]> response, MediaType contentType, Type expectedContentType) throws Exception {
        return JSONs.parse(new String(response.getPayload()), expectedContentType);
    }
}
