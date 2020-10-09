package com.jn.agileway.codec;

public class CodecException extends RuntimeException{
    public CodecException() {
        super();
    }

    public CodecException(String message) {
        super(message);
    }

    public CodecException(String message, Throwable cause) {
        super(message, cause);
    }
    public CodecException(Throwable cause) {
        super(cause);
    }
}
