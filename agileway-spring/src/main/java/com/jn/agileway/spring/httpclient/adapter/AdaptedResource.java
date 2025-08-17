package com.jn.agileway.spring.httpclient.adapter;


import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;

public class AdaptedResource implements com.jn.langx.io.resource.Resource {
    private Resource delegate;

    public AdaptedResource(Resource delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object getRealResource() {
        return delegate;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return delegate.getInputStream();
    }

    @Override
    public boolean exists() {
        return delegate.exists();
    }

    @Override
    public boolean isReadable() {
        return delegate.isReadable();
    }

    @Override
    public ReadableByteChannel readableChannel() throws IOException {
        return delegate.readableChannel();
    }

    @Override
    public long contentLength() {
        try {
            return delegate.contentLength();
        } catch (IOException e) {
            return -1;
        }
    }
}
