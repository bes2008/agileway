package com.jn.agileway.httpclient;

import com.jn.langx.util.net.http.HttpHeaders;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public interface HttpResponse extends Closeable {
    int getStatusCode();

    void close() throws IOException;

    InputStream getBody() throws IOException;

    HttpHeaders getHeaders();
}
