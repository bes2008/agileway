package com.jn.agileway.ssh.client.impl.synergy.sftp;

import com.jn.agileway.ssh.client.sftp.SftpFile;
import com.jn.agileway.ssh.client.sftp.SftpSession;
import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;
import com.jn.agileway.ssh.client.sftp.exception.SftpException;
import com.sshtools.client.sftp.SftpHandle;

import java.io.IOException;

public class SynergySftpFile extends SftpFile {
    private final SftpHandle handle;

    SynergySftpFile(SftpSession session, String path, SftpHandle handle) {
        super(session, path);
        this.handle = handle;
    }

    @Override
    public int read(long fileOffset, byte[] buffer, int bufferOffset, int length) throws IOException {
        try {
            return handle.read(fileOffset, buffer, bufferOffset, length);
        } catch (Throwable ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void write(long fileOffset, byte[] data, int offset, int length) throws IOException {
        try {
            handle.write(fileOffset, data, offset, length);
        } catch (Throwable ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void setAttributes(FileAttrs attrs) throws IOException {
        try {
            getSession().setStat(path, attrs);
        } catch (Throwable ex) {
            throw new SftpException(ex);
        }
    }

    @Override
    public FileAttrs getAttributes() throws IOException {
        try {
            return SynergySftps.fromSftpFileAttributes(handle.getAttributes());
        } catch (Throwable ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            handle.close();
        } catch (Throwable ex) {
            throw new IOException(ex);
        }
    }
}
