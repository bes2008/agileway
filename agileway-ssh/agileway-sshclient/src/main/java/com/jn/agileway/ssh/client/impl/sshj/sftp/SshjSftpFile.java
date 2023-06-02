package com.jn.agileway.ssh.client.impl.sshj.sftp;

import com.jn.agileway.ssh.client.sftp.SftpFile;
import com.jn.agileway.ssh.client.sftp.SftpSession;
import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;
import net.schmizz.sshj.sftp.RemoteFile;

import java.io.IOException;

public class SshjSftpFile extends SftpFile {
    private RemoteFile remoteFile;

    public SshjSftpFile(SftpSession session, String path) {
        super(session, path);
    }

    public void setRemoteFile(RemoteFile remoteFile) {
        this.remoteFile = remoteFile;
    }

    @Override
    public void close() throws IOException {
        this.isClosed = true;
        remoteFile.close();
    }

    @Override
    public int read(long fileOffset, byte[] buffer, int bufferOffset, int length) throws IOException {
        return remoteFile.read(fileOffset, buffer, bufferOffset, length);
    }

    @Override
    public void write(long fileOffset, byte[] data, int offset, int length) throws IOException {
        remoteFile.write(fileOffset, data, offset, length);
    }

    @Override
    public void setAttributes(FileAttrs attrs) throws IOException {
        remoteFile.setAttributes(SshjSftps.toFileAttributes(attrs));
    }

    @Override
    public FileAttrs getAttributes() throws IOException {
        return SshjSftps.fromFileAttributes(remoteFile.fetchAttributes());
    }
}
