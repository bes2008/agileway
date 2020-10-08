package com.jn.agileway.serialization;

public class SerializationException extends RuntimeException{
    public SerializationException() {
        super();
    }

    public SerializationException(String message) {
        super(message);
    }

    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }
    public SerializationException(Throwable cause) {
        super(cause);
    }
}
