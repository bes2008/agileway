package com.jn.agileway.ssh.client.impl.sshj.sftp;

import com.jn.agileway.ssh.client.sftp.OpenMode;
import com.jn.agileway.ssh.client.sftp.SftpFile;
import com.jn.agileway.ssh.client.sftp.SftpSession;
import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;
import com.jn.agileway.ssh.client.sftp.filter.SftpFileFilter;
import com.jn.agileway.ssh.client.sftp.filter.SftpResourceInfo;
import net.schmizz.sshj.sftp.RemoteFile;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.sftp.SFTPException;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class SshjSftpSession implements SftpSession {
    private SFTPClient sftpClient;

    public SshjSftpSession(SFTPClient client) {
        this.sftpClient = client;
    }

    @Override
    public int getProtocolVersion() {
        return sftpClient.version();
    }

    @Override
    public SftpFile open(String filepath, OpenMode openMode, FileAttrs attrs) throws IOException {
        return open(filepath, openMode.getCode(), attrs);
    }

    @Override
    public SftpFile open(String filepath, int openMode, final FileAttrs attrs) throws IOException {
        try {
            Set<net.schmizz.sshj.sftp.OpenMode> openModes = SshjSftps.toSshjOpenModeSet(openMode);
            net.schmizz.sshj.sftp.FileAttributes attributes = SshjSftps.toFileAttributes(attrs);
            RemoteFile remoteFile = sftpClient.open(filepath, openModes, attributes);
            SshjSftpFile file = new SshjSftpFile(this, filepath);
            file.setRemoteFile(remoteFile);
            return file;
        } catch (SFTPException ex) {
            throw SshjSftps.wrapSftpException(ex);
        }
    }

    @Override
    public void createSymlink(String src, String target) throws IOException {
        try {
            sftpClient.symlink(src, target);
        } catch (SFTPException ex) {
            throw SshjSftps.wrapSftpException(ex);
        }
    }

    @Override
    public String readLink(String path) throws IOException {
        try {
            return sftpClient.readlink(path);
        } catch (SFTPException ex) {
            throw SshjSftps.wrapSftpException(ex);
        }
    }

    @Override
    public String canonicalPath(String path) throws IOException {
        try {
            return sftpClient.canonicalize(path);
        } catch (SFTPException ex) {
            throw SshjSftps.wrapSftpException(ex);
        }
    }


    @Override
    public FileAttrs stat(String filepath) throws IOException {
        try {
            net.schmizz.sshj.sftp.FileAttributes fileAttributes = sftpClient.stat(filepath);
            return SshjSftps.fromFileAttributes(fileAttributes);
        } catch (SFTPException ex) {
            throw SshjSftps.wrapSftpException(ex);
        }
    }

    @Override
    public FileAttrs lstat(String filepath) throws IOException {
        try {
            net.schmizz.sshj.sftp.FileAttributes fileAttributes = sftpClient.lstat(filepath);
            return SshjSftps.fromFileAttributes(fileAttributes);
        } catch (SFTPException ex) {
            throw SshjSftps.wrapSftpException(ex);
        }
    }

    @Override
    public FileAttrs fstat(SftpFile file) throws IOException {
        try {
            return file.getAttributes();
        } catch (SFTPException ex) {
            throw SshjSftps.wrapSftpException(ex);
        }
    }

    @Override
    public void setStat(String path, FileAttrs attrs) throws IOException {
        try {
            sftpClient.setattr(path, SshjSftps.toFileAttributes(attrs));
        } catch (SFTPException ex) {
            throw SshjSftps.wrapSftpException(ex);
        }
    }

    @Override
    public List<SftpResourceInfo> listFiles(String directory, SftpFileFilter filter) throws IOException {
        try {
            List<RemoteResourceInfo> list = sftpClient.ls(directory);
            return null;
        } catch (SFTPException ex) {
            throw SshjSftps.wrapSftpException(ex);
        }
    }

    @Override
    public void mkdir(String directory, FileAttrs attributes) throws IOException {
        try {
            sftpClient.mkdirs(directory);
        } catch (SFTPException ex) {
            throw SshjSftps.wrapSftpException(ex);
        }
    }

    @Override
    public void rmdir(String directory) throws IOException {
        try {
            sftpClient.rmdir(directory);
        } catch (SFTPException ex) {
            throw SshjSftps.wrapSftpException(ex);
        }
    }

    @Override
    public void rm(String filepath) throws IOException {
        try {
            sftpClient.rm(filepath);
        } catch (SFTPException ex) {
            throw SshjSftps.wrapSftpException(ex);
        }
    }

    @Override
    public void mv(String oldFilepath, String newFilepath) throws IOException {
        try {
            sftpClient.rename(oldFilepath, newFilepath);
        } catch (SFTPException ex) {
            throw SshjSftps.wrapSftpException(ex);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            sftpClient.close();
        } catch (SFTPException ex) {
            throw SshjSftps.wrapSftpException(ex);
        }
    }
}
