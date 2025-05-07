package com.jn.agileway.httpclient;

import com.jn.langx.util.concurrent.promise.Promise;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public interface HttpRequest {
    HttpMethod getMethod();

    URI getUri();

    HttpHeaders getHeaders();

    void setHeaders(HttpHeaders headers);

    void setBody(OutputStream outputStream);

    HttpResponse exchange() throws IOException;
}
