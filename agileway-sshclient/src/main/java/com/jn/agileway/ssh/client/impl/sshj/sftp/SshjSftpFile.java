package com.jn.agileway.ssh.client.impl.sshj.sftp;

import com.jn.agileway.ssh.client.sftp.SftpFile;
import com.jn.agileway.ssh.client.sftp.SftpResourceInfo;
import com.jn.agileway.ssh.client.sftp.SftpSession;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.function.Predicate;
import net.schmizz.sshj.sftp.RemoteFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

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
    public void close() {
        this.isClosed = true;
    }

    @Override
    public int read(long fileOffset, byte[] buffer, int bufferOffset, int length) {
        return 0;
    }

    @Override
    public void write(long fileOffset, byte[] data, int offset, int length) {

    }

    public List<SftpResourceInfo> listFiles(Predicate<SftpResourceInfo> predicate) {
        return null;
    }

    @Override
    public boolean exist() {
        return false;
    }
}
