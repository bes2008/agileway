package com.jn.agileway.httpclient.core.serialize;

import com.jn.agileway.httpclient.core.HttpResponseBodyReader;
import com.jn.agileway.httpclient.core.UnderlyingHttpResponse;
import com.jn.easyjson.core.util.JSONs;
import com.jn.langx.util.net.mime.MediaType;

import java.io.IOException;
import java.lang.reflect.Type;

public class GeneralJsonHttpResponseReader<T> implements HttpResponseBodyReader<T> {
    @Override
    public boolean canRead(UnderlyingHttpResponse response, MediaType contentType, Type expectedBodyType) {
        if (MediaType.APPLICATION_JSON.equalsTypeAndSubtype(contentType)) {
            return true;
        }
        return false;
    }

    @Override
    public T read(UnderlyingHttpResponse response, MediaType contentType, Type expectedBodyType) throws IOException {
        return JSONs.parse(response.getBody(), expectedBodyType);
    }
}
