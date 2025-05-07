package com.jn.agileway.httpclient.jdk;

import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.langx.util.Strings;
import com.jn.langx.util.net.http.HttpHeaders;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class JdkHttpResponse implements HttpResponse {

    private HttpURLConnection httpConnection;
    private HttpHeaders headers;

    private InputStream responseStream;

    JdkHttpResponse(HttpURLConnection httpConnection) {
        this.httpConnection = httpConnection;
    }

    @Override
    public int getStatusCode() {
        try {
            return this.httpConnection.getResponseCode();
        } catch (IOException ex) {
            return -1;
        }
    }

    @Override
    public void close() throws IOException {
        this.httpConnection.disconnect();
    }

    @Override
    public InputStream getBody() throws IOException {
        if (this.responseStream == null) {
            InputStream errorStream = this.httpConnection.getErrorStream();
            this.responseStream = (errorStream != null ? errorStream : this.httpConnection.getInputStream());
        }
        return this.responseStream;
    }

    @Override
    public HttpHeaders getHeaders() {
        if (this.headers == null) {
            this.headers = new HttpHeaders();
            // Header field 0 is the status line for most HttpURLConnections, but not on GAE
            String name = this.httpConnection.getHeaderFieldKey(0);
            if (Strings.isNotBlank(name)) {
                this.headers.add(name, this.httpConnection.getHeaderField(0));
            }
            int i = 1;
            while (true) {
                name = this.httpConnection.getHeaderFieldKey(i);
                if (Strings.isBlank(name)) {
                    break;
                }
                this.headers.add(name, this.httpConnection.getHeaderField(i));
                i++;
            }
        }
        return this.headers;
    }
}
