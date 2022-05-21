package com.jn.agileway.eimessage.core.endpoint.mapper;

public class UnsupportedObjectException extends RuntimeException {
    public UnsupportedObjectException() {
        super();
    }

    public UnsupportedObjectException(String message) {
        super(message);
    }

    public UnsupportedObjectException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedObjectException(Throwable cause) {
        super(cause);
    }
}
