package com.jn.agileway.vfs;

public class VfsException extends RuntimeException {
    public VfsException() {
        super();
    }

    public VfsException(String message) {
        super(message);
    }

    public VfsException(String message, Throwable cause) {
        super(message, cause);
    }

    public VfsException(Throwable cause) {
        super(cause);
    }
}
