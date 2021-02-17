package com.jn.agileway.ssh.client.impl.sshj.sftp;

import com.jn.agileway.ssh.client.sftp.SftpFile;
import com.jn.agileway.ssh.client.sftp.SftpSession;
import com.jn.langx.annotation.Nullable;
import net.schmizz.sshj.sftp.RemoteFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SshjSftpFile extends SftpFile {
    private RemoteFile remoteFile;

    public SshjSftpFile(SftpSession session, String path) {
        super(session, path, null, null);
    }

    public SshjSftpFile(SftpSession session, String path, @Nullable InputStream inputStream, @Nullable OutputStream outputStream) {
        super(session, path, null, inputStream, outputStream);
    }

    public SshjSftpFile(SftpSession session, String path, @Nullable String fileHandle, @Nullable InputStream inputStream, @Nullable OutputStream outputStream) {
        super(session, path, fileHandle, inputStream, outputStream);
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
}
