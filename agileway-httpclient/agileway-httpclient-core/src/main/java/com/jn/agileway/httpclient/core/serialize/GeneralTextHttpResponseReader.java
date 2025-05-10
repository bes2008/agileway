package com.jn.agileway.httpclient.core.serialize;

import com.jn.agileway.httpclient.core.HttpResponseBodyReader;
import com.jn.agileway.httpclient.core.UnderlyingHttpResponse;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.net.mime.MediaType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GeneralTextHttpResponseReader implements HttpResponseBodyReader<String> {
    private List<MediaType> supportedMediaTypes = new ArrayList<MediaType>();

    public GeneralTextHttpResponseReader() {
        addSupportedMediaType(MediaType.TEXT_PLAIN);
        addSupportedMediaType(MediaType.TEXT_HTML);
        addSupportedMediaType(MediaType.TEXT_XML);
        addSupportedMediaType(MediaType.TEXT_EVENT_STREAM);
        addSupportedMediaType(MediaType.TEXT_MARKDOWN);
        addSupportedMediaType(MediaType.parseMediaType("text/css"));
        addSupportedMediaType(MediaType.parseMediaType("application/javascript"));
    }

    public void addSupportedMediaType(MediaType mediaType) {
        supportedMediaTypes.add(mediaType);
    }

    @Override
    public boolean canRead(UnderlyingHttpResponse response, MediaType contentType, Type expectedBodyType) {
        return Pipeline.<MediaType>of(supportedMediaTypes)
                .anyMatch(new Predicate<MediaType>() {
                    @Override
                    public boolean test(MediaType mediaType) {
                        return mediaType.equalsTypeAndSubtype(contentType);
                    }
                });
    }

    @Override
    public String read(UnderlyingHttpResponse response, MediaType contentType, Type expectedBodyType) throws IOException {
        return IOs.readAsString(response.getBody());
    }
}
