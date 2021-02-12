package com.jn.agileway.ssh.client.sftp.filter;

import com.jn.agileway.ssh.client.sftp.SftpFile;

public interface SftpFilenameFilter {
    /**
     * Tests if a specified file should be included in a file list.
     *
     * @param dir  the directory in which the file was found.
     * @param name the name of the file.
     * @return <code>true</code> if and only if the name should be
     * included in the file list; <code>false</code> otherwise.
     */
    boolean accept(SftpFile dir, String name);
}
