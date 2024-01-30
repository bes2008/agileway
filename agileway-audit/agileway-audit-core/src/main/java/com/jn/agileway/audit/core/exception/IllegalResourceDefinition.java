package com.jn.agileway.audit.core.exception;

public class IllegalResourceDefinition extends RuntimeException {
    public IllegalResourceDefinition() {
        super();
    }

    public IllegalResourceDefinition(String message) {
        super(message);
    }

    public IllegalResourceDefinition(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalResourceDefinition(Throwable cause) {
        super(cause);
    }
}
