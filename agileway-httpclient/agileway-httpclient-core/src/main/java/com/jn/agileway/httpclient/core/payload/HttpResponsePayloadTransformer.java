package com.jn.agileway.httpclient.core.payload;

import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.agileway.eipchannel.core.transformer.MessageTransformer;
import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.httpclient.core.MessageHeaderConstants;
import com.jn.agileway.httpclient.core.error.exception.NotFoundHttpContentReaderException;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.net.mime.MediaType;

import java.lang.reflect.Type;
import java.util.List;

public class HttpResponsePayloadTransformer implements MessageTransformer {

    private List<HttpResponsePayloadReader> readers;

    public HttpResponsePayloadTransformer(List<HttpResponsePayloadReader> readers) {
        this.readers = readers;
    }

    @Override
    public Message<?> transform(Message<?> message) {
        HttpResponse response = (HttpResponse) message;
        if (response.hasError()) {
            return response;
        }
        Object payload = response.getPayload();
        if (payload == null) {
            return response;
        }

        boolean deserialized = payload.getClass() != byte[].class;
        if (deserialized) {
            return response;
        }
        Type expectedType = (Type) response.getHeaders().get(MessageHeaderConstants.RESPONSE_KEY_REPLY_PAYLOAD_TYPE);
        if (expectedType == null) {
            expectedType = Object.class;
        }
        if (expectedType == byte[].class) {
            return response;
        }

        final MediaType contentType = Objs.useValueIfNull(response.getHttpHeaders().getContentType(), MediaType.TEXT_PLAIN);
        final Type entityType = expectedType;
        HttpResponsePayloadReader reader = Pipeline.of(readers)
                .findFirst(new Predicate<HttpResponsePayloadReader>() {
                    @Override
                    public boolean test(HttpResponsePayloadReader httpResponseBodyReader) {
                        return httpResponseBodyReader.canRead(response, contentType, entityType);
                    }
                });
        if (reader != null) {
            try {
                payload = reader.read(response, contentType, entityType);
                return new HttpResponse<>(response.getMethod(), response.getUri(), response.getStatusCode(), response.getHeaders(), null, payload);
            } catch (Exception e) {
                throw Throwables.wrapAsRuntimeException(e);
            }
        } else {
            throw new NotFoundHttpContentReaderException(StringTemplates.formatWithPlaceholder("Can't find a HttpResponseBodyReader to read the response body for Content-Type {}", contentType));
        }
    }
}
