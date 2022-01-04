package com.jn.agileway.ssh.client.transport.hostkey;

import com.jn.agileway.ssh.client.SshException;

public class UnsupportedKeyException  extends SshException {
    public UnsupportedKeyException() {
        super();
    }

    public UnsupportedKeyException(String message) {
        super(message);
    }

    public UnsupportedKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedKeyException(Throwable cause) {
        super(cause);
    }
}
