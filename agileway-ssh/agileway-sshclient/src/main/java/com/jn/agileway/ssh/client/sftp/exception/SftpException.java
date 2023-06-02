package com.jn.agileway.ssh.client.sftp.exception;

import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.sftp.ResponseStatusCode;

public class SftpException extends SshException {
    private ResponseStatusCode statusCode;

    public void setStatusCode(ResponseStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public ResponseStatusCode getStatusCode() {
        return statusCode;
    }

    public SftpException() {
        super();
    }

    public SftpException(String message) {
        super(message);
    }

    public SftpException(String message, Throwable cause) {
        super(message, cause);
    }

    public SftpException(Throwable cause) {
        super(cause);
    }
}
