package com.jn.agileway.httpclient.core.payload;

import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.net.mime.MediaType;

import java.lang.reflect.Type;
import java.nio.charset.Charset;

public class GeneralTextHttpResponseReader extends CustomMediaTypesHttpResponseReader<String> {

    public GeneralTextHttpResponseReader() {
        addSupportedMediaTypes(MediaType.parseMediaTypes("application/rss+xml, application/atom+xml, application/xml, application/javascript"));
    }

    @Override
    public boolean canRead(HttpResponse<byte[]> response, MediaType contentType, Type expectedContentType) {
        if ("text".equals(contentType.getType())) {
            return true;
        }
        return super.canRead(response, contentType, expectedContentType);
    }

    @Override
    public String read(HttpResponse<byte[]> response, MediaType contentType, Type expectedContentType) throws Exception {
        Charset charset = contentType.getCharset();
        if (charset == null) {
            charset = Charsets.UTF_8;
        }
        return new String(response.getPayload(), charset);
    }
}
