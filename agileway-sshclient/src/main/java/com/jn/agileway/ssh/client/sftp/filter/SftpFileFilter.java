package com.jn.agileway.ssh.client.sftp.filter;

import com.jn.agileway.ssh.client.sftp.SftpFile;

public interface SftpFileFilter {
    boolean accept(SftpFile dir);
}
