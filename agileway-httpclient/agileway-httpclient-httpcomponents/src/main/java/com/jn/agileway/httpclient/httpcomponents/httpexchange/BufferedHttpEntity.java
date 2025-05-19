package com.jn.agileway.httpclient.httpcomponents.httpexchange;

import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

import java.io.*;
import java.util.List;

class BufferedHttpEntity extends OutputStream implements HttpEntity {
    private Header contentType;
    private Header contentEncoding;
    private ByteArrayOutputStream buffer;

    public BufferedHttpEntity(String contentType, List<String> contentEncodings) {
        this.buffer = new ByteArrayOutputStream();
        if (Strings.isNotEmpty(contentType)) {
            this.contentType = new BasicHeader("Content-Type", contentType);
        }
        if (Objs.isNotEmpty(contentEncodings)) {
            this.contentEncoding = new BasicHeader("Content-Encoding", String.join(",", contentEncodings));
            this.buffer = HttpClientUtils.wrapByContentEncodings(buffer, HttpClientUtils.getContentEncoding(contentEncodings));
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
        if (this.contentEncoding == null) {
            return ((ByteArrayOutputStream) buffer).size();
        }
        return -1;
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
