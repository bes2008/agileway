package com.jn.agileway.httpclient.hc4.httpexchange;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.payload.HttpRequestPayloadWriter;
import com.jn.agileway.httpclient.util.ContentEncoding;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.util.Strings;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

class HttpRequestAttachmentHttpEntity implements HttpEntity {
    private HttpRequestPayloadWriter writer;
    private HttpRequest request;

    private Header contentType;


    public HttpRequestAttachmentHttpEntity(HttpRequest request, HttpRequestPayloadWriter writer, String contentType) {
        this.writer = writer;
        this.request = request;
        this.contentType = new BasicHeader("Content-Type", contentType);
    }

    @Override
    public boolean isRepeatable() {
        return false;
    }

    @Override
    public boolean isChunked() {
        return true;
    }

    @Override
    public long getContentLength() {
        return -1;
    }

    @Override
    public Header getContentType() {
        return contentType;
    }

    @Override
    public Header getContentEncoding() {
        String contentEncoding = request.getHttpHeaders().getFirst("Content-Encoding");
        return Strings.isBlank(contentEncoding) ? null : new BasicHeader("Content-Encoding", contentEncoding);
    }

    @Override
    public InputStream getContent() throws IOException, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeTo(OutputStream outStream) throws IOException {
        try {
            List<ContentEncoding> contentEncodings = HttpClientUtils.getContentEncodings(request.getHttpHeaders());
            OutputStream out = HttpClientUtils.wrapByContentEncodings(outStream, contentEncodings);
            writer.write(request, out);
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public boolean isStreaming() {
        return true;
    }

    @Override
    public void consumeContent() throws IOException {

    }
}
