package com.jn.agileway.httpclient.core.payload;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.MessageHeaderConstants;
import com.jn.agileway.httpclient.core.error.exception.NotFoundHttpContentWriterException;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class MarshallingHttpRequestPayloadTransformer extends HttpRequestPayloadTransformer {
    private List<HttpRequestPayloadWriter> writers;

    public MarshallingHttpRequestPayloadTransformer(List<HttpRequestPayloadWriter> writers) {
        this.writers = writers;
    }

    @Override
    protected HttpRequest doTransformInternal(HttpRequest request) {

        if (HttpClientUtils.isSupportContentMethod(request.getMethod()) && request.getPayload() != null) {

            HttpRequestPayloadWriter requestBodyWriter = Pipeline.of(writers)
                    .findFirst(new Predicate<HttpRequestPayloadWriter>() {
                        @Override
                        public boolean test(HttpRequestPayloadWriter writer) {
                            return writer.canWrite(request);
                        }
                    });
            if (requestBodyWriter != null) {

                // 如果是附件上传请求，则不需要进行 payload 转换，不需要当下就进行write操作，只是记录 Writer
                boolean isAttachmentUpload = Boolean.TRUE.equals(request.getHeaders().get(MessageHeaderConstants.REQUEST_KEY_IS_ATTACHMENT_UPLOAD));
                if (isAttachmentUpload) {
                    request.getHeaders().put(MessageHeaderConstants.REQUEST_KEY_ATTACHMENT_UPLOAD_WRITER, requestBodyWriter);
                } else {
                    try {
                        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                        requestBodyWriter.write(request, buffer);
                        request.setPayload(buffer);
                    } catch (Exception ex) {
                        throw Throwables.wrapAsRuntimeException(ex);
                    }
                }
            } else {
                throw new NotFoundHttpContentWriterException();
            }
        }
        return request;
    }
}
