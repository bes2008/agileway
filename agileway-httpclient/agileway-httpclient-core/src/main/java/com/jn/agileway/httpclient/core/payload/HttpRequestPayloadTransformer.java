package com.jn.agileway.httpclient.core.payload;

import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.agileway.eipchannel.core.transformer.MessageTransformer;
import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.MessageHeaderConstants;
import com.jn.agileway.httpclient.core.error.exception.NotFoundHttpContentWriterException;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class HttpRequestPayloadTransformer implements MessageTransformer {
    private List<HttpRequestPayloadWriter> writers;

    public HttpRequestPayloadTransformer(List<HttpRequestPayloadWriter> writers) {
        this.writers = writers;
    }

    @Override
    public Message<?> transform(Message<?> message) {
        HttpRequest request = (HttpRequest) message;

        if (HttpClientUtils.isWriteableMethod(request.getMethod()) && request.getPayload() != null) {

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            Object loggingPayload = request.getPayload();
            request.getHeaders().put(MessageHeaderConstants.REQUEST_KEY_LOGGING_PAYLOAD, loggingPayload);
            HttpRequestPayloadWriter requestBodyWriter = Pipeline.of(writers)
                    .findFirst(new Predicate<HttpRequestPayloadWriter>() {
                        @Override
                        public boolean test(HttpRequestPayloadWriter writer) {
                            return writer.canWrite(request);
                        }
                    });
            if (requestBodyWriter != null) {
                try {
                    requestBodyWriter.write(request, buffer);
                    request.setPayload(buffer);
                } catch (Exception ex) {
                    throw Throwables.wrapAsRuntimeException(ex);
                }
            } else {
                throw new NotFoundHttpContentWriterException();
            }
        }
        return request;
    }
}
