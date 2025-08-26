package com.jn.agileway.httpclient.core.payload;

import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.net.mime.MediaType;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class CustomMediaTypesHttpResponseReader<T> implements HttpResponsePayloadReader<T> {
    private List<MediaType> supportedMediaTypes = new ArrayList<MediaType>();

    public void addSupportedMediaType(MediaType mediaType) {
        supportedMediaTypes.add(mediaType);
    }

    public void addSupportedMediaTypes(List<MediaType> mediaTypes) {
        supportedMediaTypes.addAll(mediaTypes);
    }

    @Override
    public boolean canRead(HttpResponse<byte[]> response, MediaType contentType, Type expectedContentType) {
        return Pipeline.<MediaType>of(supportedMediaTypes)
                .anyMatch(new Predicate<MediaType>() {
                    @Override
                    public boolean test(MediaType mediaType) {
                        return mediaType.equalsTypeAndSubtype(contentType);
                    }
                });
    }

}
