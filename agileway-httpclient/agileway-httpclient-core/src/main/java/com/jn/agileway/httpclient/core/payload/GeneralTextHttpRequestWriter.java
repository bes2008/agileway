package com.jn.agileway.httpclient.core.payload;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.net.mime.MediaType;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;

public class GeneralTextHttpRequestWriter implements HttpRequestPayloadWriter {
    private List<MediaType> supportedMediaTypes;

    public GeneralTextHttpRequestWriter(List<MediaType> textMediaTypes) {
        this.supportedMediaTypes = Objs.useValueIfNull(textMediaTypes, Lists.immutableList());
    }

    @Override
    public boolean canWrite(HttpRequest<?> request) {
        MediaType contentType = request.getHttpHeaders().getContentType();
        if ("text".equals(contentType.getType())) {
            return true;
        }
        return Pipeline.<MediaType>of(supportedMediaTypes)
                .anyMatch(new Predicate<MediaType>() {
                    @Override
                    public boolean test(MediaType mediaType) {
                        return mediaType.equalsTypeAndSubtype(contentType);
                    }
                });
    }

    @Override
    public void write(HttpRequest<?> request, OutputStream output) throws Exception {
        MediaType contentType = request.getHttpHeaders().getContentType();
        Charset charset = contentType.getCharset();
        if (charset == null) {
            charset = Charsets.UTF_8;
        }
        String content = request.getPayload().toString();
        output.write(content.getBytes(charset));
    }
}
