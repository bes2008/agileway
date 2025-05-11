package com.jn.agileway.httpclient.core.serialize;

import com.jn.agileway.httpclient.core.HttpResponseBodyReader;
import com.jn.agileway.httpclient.core.UnderlyingHttpResponse;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.net.mime.MediaType;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class CustomMediaTypesHttpResponseReader<T> implements HttpResponseBodyReader<T> {
    private List<MediaType> supportedMediaTypes = new ArrayList<MediaType>();

    public void addSupportedMediaType(MediaType mediaType) {
        supportedMediaTypes.add(mediaType);
    }

    public void addSupportedMediaTypes(List<MediaType> mediaTypes) {
        supportedMediaTypes.addAll(mediaTypes);
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

}
