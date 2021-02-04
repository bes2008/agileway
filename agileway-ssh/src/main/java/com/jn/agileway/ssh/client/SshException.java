package com.jn.agileway.ssh.client;

public class SshException extends RuntimeException {
    public SshException() {
        super();
    }

    public SshException(String message) {
        super(message);
    }

    public SshException(String message, Throwable cause) {
        super(message, cause);
    }

    public SshException(Throwable cause) {
        super(cause);
    }
}
