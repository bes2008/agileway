package com.jn.agileway.httpclient.okhttp;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.payload.HttpRequestPayloadWriter;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

class HttpRequestPayload extends RequestBody {
    private HttpRequestPayloadWriter writer;
    private HttpRequest request;
    private MediaType mediaType;

    public HttpRequestPayload(HttpRequest request, HttpRequestPayloadWriter writer, MediaType mediaType) {
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
            writer.write(request, bufferedSink.outputStream());
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
