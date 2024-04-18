package com.jn.agileway.zip;

public class UnsupportedArchiveFormatException extends RuntimeException {
    public UnsupportedArchiveFormatException() {
        super();
    }

    public UnsupportedArchiveFormatException(String message) {
        super(message);
    }

    public UnsupportedArchiveFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedArchiveFormatException(Throwable cause) {
        super(cause);
    }
}
