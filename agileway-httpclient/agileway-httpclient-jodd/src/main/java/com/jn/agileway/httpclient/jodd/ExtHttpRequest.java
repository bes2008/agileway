package com.jn.agileway.httpclient.jodd;

import jodd.http.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static jodd.util.StringPool.CRLF;
import static jodd.util.StringPool.SPACE;

public class ExtHttpRequest extends HttpRequest {
    @Override
    public HttpResponse send() {
        if (!followRedirects) {
            return sendInternal();
        }

        int redirects = this.maxRedirects;

        while (redirects > 0) {
            redirects--;

            final HttpResponse httpResponse = sendInternal();

            final int statusCode = httpResponse.statusCode();

            if (HttpStatus.isRedirect(statusCode)) {
                sendInternal();
                final String location = httpResponse.location();
                if (location == null) {
                    return httpResponse;
                }
                set(location);
                continue;
            }

            return httpResponse;
        }

        throw new HttpException("Max number of redirects exceeded: " + this.maxRedirects);
    }

    private HttpResponse sendInternal() {
        if (httpConnection == null) {
            open();
        }

        // sends data
        final HttpResponse httpResponse;
        try {
            final OutputStream outputStream = httpConnection.getOutputStream();

            sendTo(outputStream);

            final InputStream inputStream = httpConnection.getInputStream();

            httpResponse = HttpResponse.readFrom(inputStream);

            //TODO httpResponse.httpRequest(this);
        } catch (final IOException ioex) {
            throw new HttpException(ioex);
        }

        final boolean keepAlive = httpResponse.isConnectionPersistent();

        if (!keepAlive) {
            // closes connection if keep alive is false, or if counter reached 0
            httpConnection.close();
            httpConnection = null;
        }

        return httpResponse;
    }

    @Override
    protected Buffer buffer(boolean fullRequest) {
        // INITIALIZATION

        // host port

        if (header(HEADER_HOST) == null) {
            setHostHeader();
        }

        // form

        final Buffer formBuffer = formBuffer();

        // query string

        final String queryString = queryString();

        // user-agent

//		if (header("User-Agent") == null) {
//			header("User-Agent", Defaults.userAgent);
//		}

        // POST method requires Content-Type to be set

        if (method.equals("POST") && (contentLength() == null)) {
            contentLength(0);
        }


        // BUILD OUT

        final Buffer request = new Buffer();

        request.append(method)
                .append(SPACE)
                .append(path);

        if (query != null && !query.isEmpty()) {
            request.append('?');
            request.append(queryString);
        }

        request.append(SPACE)
                .append(httpVersion)
                .append(CRLF);

        populateHeaderAndBody(request, formBuffer, fullRequest);

        return request;
    }
}
