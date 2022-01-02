package com.jn.agileway.ssh.client.transport.hostkey.knownhosts;

import com.jn.agileway.ssh.client.SshException;

public class UnsupportedHostsKeyTypeException extends SshException {
    public UnsupportedHostsKeyTypeException() {
        super();
    }

    public UnsupportedHostsKeyTypeException(String message) {
        super(message);
    }

    public UnsupportedHostsKeyTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedHostsKeyTypeException(Throwable cause) {
        super(cause);
    }
}
