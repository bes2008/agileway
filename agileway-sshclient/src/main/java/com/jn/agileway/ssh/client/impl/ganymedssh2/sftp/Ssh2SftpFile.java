package com.jn.agileway.ssh.client.impl.ganymedssh2.sftp;

import com.jn.agileway.ssh.client.sftp.SftpFile;
import com.jn.agileway.ssh.client.sftp.SftpSession;
import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;

import java.io.IOException;

public class Ssh2SftpFile extends SftpFile {

    public Ssh2SftpFile(SftpSession session, String path) {
        super(session, path);
    }

    @Override
    public int read(long fileOffset, byte[] buffer, int bufferOffset, int length) throws IOException {
        return 0;
    }

    @Override
    public void write(long fileOffset, byte[] data, int offset, int length) throws IOException {

    }

    @Override
    public void setAttributes(FileAttrs attrs) throws IOException {

    }

    @Override
    public FileAttrs getAttributes() throws IOException {
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
