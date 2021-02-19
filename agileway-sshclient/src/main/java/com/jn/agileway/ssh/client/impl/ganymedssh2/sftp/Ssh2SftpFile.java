package com.jn.agileway.ssh.client.impl.ganymedssh2.sftp;

import ch.ethz.ssh2.SFTPException;
import ch.ethz.ssh2.SFTPv3Client;
import ch.ethz.ssh2.SFTPv3FileAttributes;
import ch.ethz.ssh2.SFTPv3FileHandle;
import com.jn.agileway.ssh.client.sftp.SftpFile;
import com.jn.agileway.ssh.client.sftp.SftpSession;
import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;

import java.io.IOException;

public class Ssh2SftpFile extends SftpFile {
    private SFTPv3FileHandle fileHandle;

    public Ssh2SftpFile(SftpSession session, String path) {
        super(session, path);
    }

    public void setFileHandle(SFTPv3FileHandle fileHandle) {
        this.fileHandle = fileHandle;
    }

    @Override
    public int read(long fileOffset, byte[] buffer, int bufferOffset, int length) throws IOException {
        SFTPv3Client sftpClient = ((Ssh2SftpSession) this.session).getSftpClient();
        try {
            return sftpClient.read(fileHandle, fileOffset, buffer, bufferOffset, length);
        } catch (SFTPException ex) {
            throw Ssh2Sftps.wrapSftpException(ex);
        }
    }

    @Override
    public void write(long fileOffset, byte[] data, int offset, int length) throws IOException {
        SFTPv3Client sftpClient = ((Ssh2SftpSession) this.session).getSftpClient();
        try {
            sftpClient.write(fileHandle, fileOffset, data, offset, length);
        } catch (SFTPException ex) {
            throw Ssh2Sftps.wrapSftpException(ex);
        }
    }

    @Override
    public void setAttributes(FileAttrs attrs) throws IOException {
        try {
            session.setStat(path, attrs);
        } catch (SFTPException ex) {
            throw Ssh2Sftps.wrapSftpException(ex);
        }
    }

    @Override
    public FileAttrs getAttributes() throws IOException {
        SFTPv3Client sftpClient = ((Ssh2SftpSession) this.session).getSftpClient();
        try {
            SFTPv3FileAttributes attributes = sftpClient.fstat(fileHandle);
            return Ssh2Sftps.fromSsh2FileAttributes(attributes);
        } catch (SFTPException ex) {
            throw Ssh2Sftps.wrapSftpException(ex);
        }
    }

    @Override
    public void close() throws IOException {
        SFTPv3Client sftpClient = ((Ssh2SftpSession) this.session).getSftpClient();
        try {
            sftpClient.closeFile(fileHandle);
        } catch (SFTPException ex) {
            throw Ssh2Sftps.wrapSftpException(ex);
        }
    }
}
