package com.jn.agileway.httpclient.core.payload;

import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.agileway.eipchannel.core.transformer.MessageTransformer;
import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.error.exception.NotFoundHttpContentWriterException;
import com.jn.agileway.httpclient.util.HttpClientUtils;
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

        if (HttpClientUtils.isWriteable(request.getMethod()) && request.getPayload() != null) {

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            HttpRequestPayloadWriter requestBodyWriter = Pipeline.of(writers)
                    .findFirst(new Predicate<HttpRequestPayloadWriter>() {
                        @Override
                        public boolean test(HttpRequestPayloadWriter writer) {
                            return writer.canWrite(request);
                        }
                    });
            if (requestBodyWriter != null) {
                requestBodyWriter.write(request, buffer);
                request.setPayload(buffer);
            } else {
                throw new NotFoundHttpContentWriterException();
            }
        }
        return request;
    }
}
