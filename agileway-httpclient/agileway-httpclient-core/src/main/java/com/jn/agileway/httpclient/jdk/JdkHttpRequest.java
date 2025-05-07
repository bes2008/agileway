package com.jn.agileway.httpclient.jdk;

import com.jn.agileway.httpclient.core.AbstractHttpRequest;
import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Strings;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;

public class JdkHttpRequest extends AbstractHttpRequest {

    private HttpURLConnection httpConnection;

    JdkHttpRequest(HttpURLConnection httpConnection) {
        this.httpConnection = httpConnection;
    }


    @Override
    public HttpMethod getMethod() {
        return HttpMethod.resolve(this.httpConnection.getRequestMethod());
    }

    @Override
    public URI getUri() {
        try {
            return this.httpConnection.getURL().toURI();
        } catch (URISyntaxException ex) {
            throw new IllegalStateException(StringTemplates.formatWithPlaceholder("Error occur when get HttpURLConnection URI: {}", ex.getMessage()), ex);
        }
    }

    @Override
    protected HttpResponse exchangeInternal() throws IOException {
        addHeaders(this.httpConnection, this.getHeaders());
        this.httpConnection.connect();
        if (this.httpConnection.getDoOutput()) {
            OutputStream out = this.httpConnection.getOutputStream();
            out.write(this.getBody());
        } else {
            // Immediately trigger the request in a no-output scenario as well
            this.httpConnection.getResponseCode();
        }
        return new JdkHttpResponse(this.httpConnection);
    }


    /**
     * Add the given headers to the given HTTP connection.
     *
     * @param connection the connection to add the headers to
     * @param headers    the headers to add
     */
    static void addHeaders(HttpURLConnection connection, HttpHeaders headers) {
        String method = connection.getRequestMethod();
        if (method.equals("PUT") || method.equals("DELETE")) {
            if (Strings.isBlank(headers.getFirst(HttpHeaders.ACCEPT))) {
                // Avoid "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2"
                // from HttpUrlConnection which prevents JSON error response details.
                headers.set(HttpHeaders.ACCEPT, "*/*");
            }
        }
        headers.forEach((headerName, headerValues) -> {
            if (HttpHeaders.COOKIE.equalsIgnoreCase(headerName)) {  // RFC 6265
                String headerValue = Strings.join("; ", headerValues);
                connection.setRequestProperty(headerName, headerValue);
            } else {
                for (String headerValue : headerValues) {
                    String actualHeaderValue = headerValue != null ? headerValue : "";
                    connection.addRequestProperty(headerName, actualHeaderValue);
                }
            }
        });
    }
}
