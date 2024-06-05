package com.jn.agileway.web.servlet;

import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.io.IOs;

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
        try {
            this.getInputStream();
        } catch (Throwable ex) {
            // ignore it
        }
    }

    private byte[] requestBodyBackup;
    private ByteBuffer requestBody;// for input stream

    @Override
    public ServletInputStream getInputStream() throws IOException {
        ServletInputStream inputStream = super.getInputStream();
        if (requestBody == null) {
            int length = this.getContentLength();
            if (length == 0) {
                requestBodyBackup = new byte[0];
            } else {
                requestBodyBackup = IOs.toByteArray(inputStream);
            }
            requestBody = ByteBuffer.wrap(requestBodyBackup);
            requestBody.rewind();
        }

        return new ServletInputStream() {
            @Override
            public int read() throws IOException {
                if (requestBody == null || !requestBody.hasRemaining()) {
                    return -1;
                }
                return IOs.filterInputStreamRead(requestBody.get());
            }
        };
    }

    public byte[] getRequestBody() {
        return requestBodyBackup;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream(), Charsets.UTF_8));
    }
}
