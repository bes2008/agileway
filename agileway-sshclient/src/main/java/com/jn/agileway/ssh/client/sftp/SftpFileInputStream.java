package com.jn.agileway.ssh.client.sftp;

import java.io.*;

public class SftpFileInputStream extends InputStream {

    private SftpFile sftpFile;
    private transient byte[] buf;

    public SftpFileInputStream(SftpFile sftpFile){
        this.sftpFile = sftpFile;
        this.buf = new byte[4096];
    }

    @Override
    public int read() throws IOException {
        return 0;
    }
}
