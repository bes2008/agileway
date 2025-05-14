package com.jn.agileway.httpclient.httpcomponents.impl;

import com.jn.agileway.httpclient.util.ContentEncoding;
import com.jn.langx.util.Strings;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

import java.io.*;

public class BufferedHttpEntity extends OutputStream implements HttpEntity {
    private Header contentType;
    private Header contentEncoding;
    private ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    public BufferedHttpEntity(String contentType, ContentEncoding contentEncoding) {
        if (contentEncoding != null) {
            this.contentEncoding = new BasicHeader("Content-Encoding", contentEncoding.getName());
        }
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
        return contentEncoding;
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
    }

    @Override
    public boolean isStreaming() {
        return false;
    }

    @Override
    public void consumeContent() throws IOException {

    }

    @Override
    public void write(int b) throws IOException {
        buffer.write(b);
    }
}
