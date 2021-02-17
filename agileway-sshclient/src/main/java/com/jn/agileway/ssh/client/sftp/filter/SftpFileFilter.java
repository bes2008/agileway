package com.jn.agileway.ssh.client.sftp.filter;

public interface SftpFileFilter {
    boolean accept(SftpResourceInfo resource);
}
