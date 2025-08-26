package com.jn.agileway.httpclient.okhttp;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.payload.HttpRequestPayloadWriter;
import com.jn.agileway.httpclient.util.ContentEncoding;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

class HttpRequestAttachmentPayload extends RequestBody {
    private HttpRequestPayloadWriter writer;
    private HttpRequest request;
    private MediaType mediaType;

    public HttpRequestAttachmentPayload(HttpRequest request, HttpRequestPayloadWriter writer, MediaType mediaType) {
        this.writer = writer;
        this.request = request;
        this.mediaType = mediaType;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return this.mediaType;
    }

    @Override
    public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {
        try {
            List<ContentEncoding> contentEncodings = HttpClientUtils.getContentEncodings(request.getHttpHeaders());
            OutputStream out = HttpClientUtils.wrapByContentEncodings(bufferedSink.outputStream(), contentEncodings);
            writer.write(request, out);
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public boolean isOneShot() {
        return true;
    }

    @Override
    public boolean isDuplex() {
        return false;
    }
}
