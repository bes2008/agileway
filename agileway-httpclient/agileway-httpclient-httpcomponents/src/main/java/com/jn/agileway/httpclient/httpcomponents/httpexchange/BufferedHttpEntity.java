package com.jn.agileway.httpclient.httpcomponents.httpexchange;

import com.jn.langx.util.Strings;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

import java.io.*;

class BufferedHttpEntity implements HttpEntity {
    private Header contentType;
    private ByteArrayOutputStream buffer;

    public BufferedHttpEntity(ByteArrayOutputStream buffer, String contentType) {
        this.buffer = buffer;
        if (Strings.isNotEmpty(contentType)) {
            this.contentType = new BasicHeader("Content-Type", contentType);
        }
    }


    @Override
    public boolean isRepeatable() {
        return true;
    }

    @Override
    public boolean isChunked() {
        return false;
    }

    @Override
    public long getContentLength() {
        return buffer.size();
    }

    @Override
    public Header getContentType() {
        return contentType;
    }

    @Override
    public Header getContentEncoding() {
        return null;
    }

    @Override
    public InputStream getContent() throws IOException, UnsupportedOperationException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer.toByteArray());
        return inputStream;
    }

    @Override
    public void writeTo(OutputStream outStream) throws IOException {
        byte[] bytes = this.buffer.toByteArray();
        outStream.write(bytes);
        outStream.flush();
    }

    @Override
    public boolean isStreaming() {
        return false;
    }

    @Override
    public void consumeContent() throws IOException {

    }
}
