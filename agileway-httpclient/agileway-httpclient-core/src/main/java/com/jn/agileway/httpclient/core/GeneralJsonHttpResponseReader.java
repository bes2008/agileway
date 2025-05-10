package com.jn.agileway.httpclient.core;

import com.jn.easyjson.core.util.JSONs;
import com.jn.langx.util.net.mime.MediaType;

import java.io.IOException;
import java.lang.reflect.Type;

public class GeneralJsonHttpResponseReader<T> implements HttpResponseBodyReader {
    @Override
    public boolean canRead(UnderlyingHttpResponse response, MediaType contentType, Type expectedBodyType) {
        if (MediaType.APPLICATION_JSON.equalsTypeAndSubtype(contentType)) {
            return true;
        }
        return false;
    }

    @Override
    public <T> T read(UnderlyingHttpResponse response, MediaType contentType, Type expectedBodyType) throws IOException {
        return JSONs.parse(response.getBody(), expectedBodyType);
    }
}
