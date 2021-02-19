package com.jn.agileway.ssh.client.impl.ganymedssh2.sftp;

import ch.ethz.ssh2.SFTPException;
import ch.ethz.ssh2.SFTPv3Client;
import ch.ethz.ssh2.SFTPv3FileAttributes;
import com.jn.agileway.ssh.client.sftp.AbstractSftpSession;
import com.jn.agileway.ssh.client.sftp.SftpFile;
import com.jn.agileway.ssh.client.sftp.SftpResourceInfo;
import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;
import com.jn.agileway.ssh.client.sftp.attrs.FileMode;
import com.jn.agileway.ssh.client.sftp.exception.SftpException;

import java.io.IOException;
import java.util.List;

public class Ssh2SftpSession extends AbstractSftpSession {
    private SFTPv3Client sftpClient;

    public Ssh2SftpSession(SFTPv3Client sftpClient) {
        this.sftpClient = sftpClient;
    }

    @Override
    protected List<SftpResourceInfo> doListFiles(String directory) throws IOException {
        return null;
    }

    @Override
    public int getProtocolVersion() throws SftpException {
        return sftpClient.getProtocolVersion();
    }

    @Override
    public SftpFile open(String filepath, int openMode, FileAttrs attrs) throws IOException {

    }

    @Override
    public void createSymlink(String src, String target) throws IOException {
        try {
            sftpClient.createSymlink(src, target);
        } catch (SFTPException ex) {
            throw Ssh2Sftps.wrapSftpException(ex);
        }
    }

    @Override
    public String readLink(String path) throws IOException {
        try {
            return sftpClient.readLink(path);
        } catch (SFTPException ex) {
            throw Ssh2Sftps.wrapSftpException(ex);
        }
    }

    @Override
    public String canonicalPath(String path) throws IOException {
        try {
            return sftpClient.canonicalPath(path);
        } catch (SFTPException ex) {
            throw Ssh2Sftps.wrapSftpException(ex);
        }
    }

    @Override
    public FileAttrs stat(String filepath) throws IOException {
        try {
            SFTPv3FileAttributes attributes = sftpClient.stat(filepath);
            return Ssh2Sftps.fromSsh2FileAttributes(attributes);
        } catch (SFTPException ex) {
            throw Ssh2Sftps.wrapSftpException(ex);
        }
    }

    @Override
    public FileAttrs lstat(String filepath) throws IOException {
        try {
            SFTPv3FileAttributes attributes = sftpClient.lstat(filepath);
            return Ssh2Sftps.fromSsh2FileAttributes(attributes);
        } catch (SFTPException ex) {
            throw Ssh2Sftps.wrapSftpException(ex);
        }
    }

    @Override
    public void setStat(String path, FileAttrs attrs) throws IOException {
        try {
            SFTPv3FileAttributes attributes = Ssh2Sftps.toSsh2FileAttributes(attrs);
            sftpClient.setstat(path, attributes);
        } catch (SFTPException ex) {
            throw Ssh2Sftps.wrapSftpException(ex);
        }
    }

    @Override
    public void mkdir(String directory, FileAttrs attributes) throws IOException {
        try {
            FileMode fileMode = attributes == null ? null : attributes.getFileMode();
            int permissions = fileMode == null ? 0 : fileMode.getMask();
            sftpClient.mkdir(directory, permissions);
        } catch (SFTPException ex) {
            throw Ssh2Sftps.wrapSftpException(ex);
        }
    }

    @Override
    public void rmdir(String directory) throws IOException {
        try {
            sftpClient.rmdir(directory);
        } catch (SFTPException ex) {
            throw Ssh2Sftps.wrapSftpException(ex);
        }
    }

    @Override
    public void rm(String filepath) throws IOException {
        try {
            sftpClient.rm(filepath);
        } catch (SFTPException ex) {
            throw Ssh2Sftps.wrapSftpException(ex);
        }
    }

    @Override
    public void mv(String oldFilepath, String newFilepath) throws IOException {
        try {
            sftpClient.mv(oldFilepath, newFilepath);
        } catch (SFTPException ex) {
            throw Ssh2Sftps.wrapSftpException(ex);
        }
    }

    @Override
    public void close() throws IOException {
        sftpClient.close();
    }
}
