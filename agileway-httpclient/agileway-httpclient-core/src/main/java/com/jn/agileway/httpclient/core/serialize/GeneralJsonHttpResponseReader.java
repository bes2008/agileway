package com.jn.agileway.httpclient.core.serialize;

import com.jn.agileway.httpclient.core.HttpResponseContentReader;
import com.jn.agileway.httpclient.core.UnderlyingHttpResponse;
import com.jn.easyjson.core.util.JSONs;
import com.jn.langx.util.net.mime.MediaType;

import java.io.IOException;
import java.lang.reflect.Type;

public class GeneralJsonHttpResponseReader<T> implements HttpResponseContentReader<T> {
    @Override
    public boolean canRead(UnderlyingHttpResponse response, MediaType contentType, Type expectedContentType) {
        if (MediaType.APPLICATION_JSON.equalsTypeAndSubtype(contentType)) {
            return true;
        }
        return false;
    }

    @Override
    public T read(UnderlyingHttpResponse response, MediaType contentType, Type expectedContentType) throws IOException {
        return JSONs.parse(response.getContent(), expectedContentType);
    }
}
