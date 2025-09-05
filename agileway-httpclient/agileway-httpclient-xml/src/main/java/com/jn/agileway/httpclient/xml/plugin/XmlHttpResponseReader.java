package com.jn.agileway.httpclient.xml.plugin;

import com.jn.agileway.codec.serialization.xml.OXMs;
import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.httpclient.core.payload.HttpResponsePayloadReader;
import com.jn.langx.util.net.mime.MediaType;
import com.jn.langx.util.reflect.type.Types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class XmlHttpResponseReader implements HttpResponsePayloadReader {

    @Override
    public boolean canRead(HttpResponse response, MediaType contentType, Type expectedContentType) {
        if (contentType == null) {
            return false;
        }
        if (!MediaType.APPLICATION_XML.equalsTypeAndSubtype(contentType)) {
            return false;
        }
        if (expectedContentType instanceof ParameterizedType) {
            return false;
        }
        return OXMs.isCanSerialize(Types.toClass(expectedContentType));
    }

    @Override
    public Object read(HttpResponse response, MediaType contentType, Type expectedContentType) throws Exception {
        return null;
    }
}
