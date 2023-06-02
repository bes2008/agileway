package com.jn.agileway.ssh.client.transport.hostkey;

import com.jn.agileway.ssh.client.SshException;

public class IllegalSshKeyException extends SshException {
    public IllegalSshKeyException() {
    }

    public IllegalSshKeyException(String message) {
        super(message);
    }

    public IllegalSshKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalSshKeyException(Throwable cause) {
        super(cause);
    }
}
