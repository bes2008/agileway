package com.jn.agileway.httpclient;

import com.jn.langx.util.Preconditions;
import com.jn.langx.util.concurrent.promise.Promise;
import com.jn.langx.util.net.http.HttpHeaders;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public abstract class AbstractBufferedHttpRequest implements HttpRequest {

    private final HttpHeaders headers = new HttpHeaders();
    private boolean executed = false;

    private ByteArrayOutputStream bufferedOutput = new ByteArrayOutputStream(1024);

    @Override
    public void setBody(byte[] body) {

    }

    protected OutputStream getBody() throws IOException {
        return bufferedOutput;
    }

    @Override
    public Promise<HttpResponse> execute() throws IOException {
        Preconditions.checkState(!executed, "http already executed");
        Promise<HttpResponse> promise = this.doExecute();
        this.executed = true;
        return promise;
    }

    protected abstract Promise<HttpResponse> doExecute() throws IOException;
}
