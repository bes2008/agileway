package com.jn.agileway.httpclient.core;

import java.io.IOException;

public class NotFoundHttpRequestBodyWriterException extends IOException {
    public NotFoundHttpRequestBodyWriterException() {
        super();
    }

    public NotFoundHttpRequestBodyWriterException(String message) {
        super(message);
    }
}
