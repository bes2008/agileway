package com.jn.agileway.httpclient.core.content;

import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.net.mime.MediaType;

import java.io.IOException;
import java.lang.reflect.Type;

public class GeneralTextHttpResponseReader extends CustomMediaTypesHttpResponseReader<String> {

    public GeneralTextHttpResponseReader() {
        addSupportedMediaTypes(MediaType.parseMediaTypes("application/rss+xml, application/atom+xml, application/xml, application/javascript"));
    }

    @Override
    public boolean canRead(UnderlyingHttpResponse response, MediaType contentType, Type expectedContentType) {
        if ("text".equals(contentType.getType())) {
            return true;
        }
        return super.canRead(response, contentType, expectedContentType);
    }

    @Override
    public String read(UnderlyingHttpResponse response, MediaType contentType, Type expectedContentType) throws Exception {
        return IOs.readAsString(response.getContent());
    }
}
