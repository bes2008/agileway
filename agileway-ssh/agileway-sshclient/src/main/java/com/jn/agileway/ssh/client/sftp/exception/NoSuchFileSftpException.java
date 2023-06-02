package com.jn.agileway.ssh.client.sftp.exception;

import com.jn.agileway.ssh.client.sftp.ResponseStatusCode;

public class NoSuchFileSftpException extends SftpException {
    public NoSuchFileSftpException() {
        setStatusCode(ResponseStatusCode.NO_SUCH_FILE);
    }

    public NoSuchFileSftpException(String message) {
        super(message);
        setStatusCode(ResponseStatusCode.NO_SUCH_FILE);
    }

    public NoSuchFileSftpException(String message, Throwable cause) {
        super(message, cause);
        setStatusCode(ResponseStatusCode.NO_SUCH_FILE);
    }

    public NoSuchFileSftpException(Throwable cause) {
        super(cause);
        setStatusCode(ResponseStatusCode.NO_SUCH_FILE);
    }
}
