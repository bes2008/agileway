package com.jn.agileway.httpclient.apachehttpclient4.httpexchange;

import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.collection.Lists;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

import java.io.*;

public class CompressedBufferedHttpEntity extends OutputStream implements HttpEntity {
    private Header contentType;
    private Header contentEncoding;
    private ByteArrayOutputStream underlying;
    private OutputStream compressedOutputStream;

    public CompressedBufferedHttpEntity(String contentType, String contentEncoding) {
        Preconditions.checkNotEmpty(contentEncoding, "the argument contentEncoding is required");
        this.underlying = new ByteArrayOutputStream();
        if (Strings.isNotEmpty(contentType)) {
            this.contentType = new BasicHeader("Content-Type", contentType);
        }
        this.contentEncoding = new BasicHeader("Content-Encoding", contentEncoding);
        try {
            this.compressedOutputStream = HttpClientUtils.wrapByContentEncodings(underlying, HttpClientUtils.getContentEncodings(Lists.newArrayList(Strings.split(contentEncoding, ","))));
        } catch (IOException e) {
            throw Throwables.wrapAsRuntimeIOException(e);
        }
    }

    @Override
    public void write(int b) throws IOException {
        this.compressedOutputStream.write(b);
    }

    @Override
    public boolean isRepeatable() {
        return false;
    }

    @Override
    public boolean isChunked() {
        return false;
    }

    @Override
    public long getContentLength() {
        return -1;
    }

    @Override
    public Header getContentType() {
        return this.contentType;
    }

    @Override
    public Header getContentEncoding() {
        return this.contentEncoding;
    }

    @Override
    public InputStream getContent() throws IOException, UnsupportedOperationException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(underlying.toByteArray());
        return inputStream;
    }

    @Override
    public void writeTo(OutputStream outStream) throws IOException {
        byte[] bytes = this.underlying.toByteArray();
        outStream.write(bytes);
    }

    @Override
    public boolean isStreaming() {
        return true;
    }

    @Override
    public void consumeContent() throws IOException {

    }
}
