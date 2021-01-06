package com.jn.agileway.web.servlet;

import com.jn.langx.util.io.Charsets;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

public class HttpServletRequestStreamWrapper extends HttpServletRequestWrapper {

    public HttpServletRequestStreamWrapper(HttpServletRequest request) {
        super(request);
    }

    private ByteBuffer requestBody;

    @Override
    public ServletInputStream getInputStream() throws IOException {
        ServletInputStream inputStream = super.getInputStream();

        if (requestBody == null) {
            int length = this.getContentLength();
            if (length > 0) {
                byte[] bytes = new byte[this.getContentLength()];
                inputStream.read(bytes);
                requestBody = ByteBuffer.wrap(bytes);
            } else {
                requestBody = ByteBuffer.wrap(new byte[0]);
            }
        }
        requestBody.reset();
        return new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return requestBody.get();
            }
        };
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream(), Charsets.UTF_8));
    }
}
