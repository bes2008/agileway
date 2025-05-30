package com.jn.agileway.httpclient.core.payload;

import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.agileway.eipchannel.core.transformer.MessageTransformer;
import com.jn.agileway.httpclient.core.BaseHttpMessage;
import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.httpclient.core.error.exception.NotFoundHttpContentReaderException;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.net.mime.MediaType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class HttpResponsePayloadTransformer implements MessageTransformer {

    private List<HttpResponsePayloadReader> readers;
    @Override
    public Message<?> transform(Message<?> message) {
        HttpResponse response = (HttpResponse) message;
        Object payload = response.getPayload();
        if (payload == null) {
            return response;
        }

        boolean deserialized = payload.getClass() != byte[].class;
        if (deserialized) {
            return response;
        }
        Type expectedType = (Type) response.getHeaders().get(BaseHttpMessage.HEADER_KEY_REPLY_PAYLOAD_TYPE);
        if (expectedType == null) {
            expectedType = Object.class;
        }
        if (expectedType == byte[].class) {
            return response;
        }

        final MediaType contentType = Objs.useValueIfNull(response.getHttpHeaders().getContentType(), MediaType.TEXT_PLAIN);
        HttpResponsePayloadReader reader = Pipeline.of(readers)
                .findFirst(new Predicate<HttpResponsePayloadReader>() {
                    @Override
                    public boolean test(HttpResponsePayloadReader httpResponseBodyReader) {
                        return httpResponseBodyReader.canRead(response, contentType, expectedType);
                    }
                });
        if (reader != null) {
            payload = reader.read(response, contentType, expectedType);
            response.set
        } else {
            throw new NotFoundHttpContentReaderException(StringTemplates.formatWithPlaceholder("Can't find a HttpResponseBodyReader to read the response body for Content-Type {}", contentType));
        }
    }
}
