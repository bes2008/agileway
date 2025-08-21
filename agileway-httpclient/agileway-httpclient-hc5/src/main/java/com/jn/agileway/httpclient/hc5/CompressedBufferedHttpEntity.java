package com.jn.agileway.httpclient.hc5;

import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.collection.Lists;
import org.apache.hc.core5.function.Supplier;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.message.BasicHeader;

import java.io.*;
import java.util.List;
import java.util.Set;

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
    public Set<String> getTrailerNames() {
        return null;
    }

    @Override
    public long getContentLength() {
        return -1;
    }

    @Override
    public String getContentType() {
        return this.contentType.getValue();
    }

    @Override
    public String getContentEncoding() {
        return this.contentEncoding.getValue();
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
    public Supplier<List<? extends Header>> getTrailers() {
        return null;
    }

}
