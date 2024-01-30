package com.jn.agileway.web.security;

public class AccessDeniedException extends SecurityException {
    public AccessDeniedException(String msg) {
        super(msg);
    }

    public AccessDeniedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
