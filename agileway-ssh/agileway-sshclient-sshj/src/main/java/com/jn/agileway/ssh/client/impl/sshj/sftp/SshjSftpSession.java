package com.jn.agileway.ssh.client.impl.sshj.sftp;

import com.jn.agileway.ssh.client.sftp.AbstractSftpSession;
import com.jn.agileway.ssh.client.sftp.SftpFile;
import com.jn.agileway.ssh.client.sftp.SftpResourceInfo;
import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;
import com.jn.agileway.ssh.client.sftp.exception.SftpException;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Predicate;
import net.schmizz.sshj.sftp.RemoteFile;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.sftp.SFTPException;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class SshjSftpSession extends AbstractSftpSession {
    private SFTPClient sftpClient;

    public SshjSftpSession(SFTPClient client) {
        this.sftpClient = client;
    }

    @Override
    public int getProtocolVersion() throws SftpException {
        return sftpClient.version();
    }

    @Override
    public SftpFile open(String filepath, int openMode, final FileAttrs attrs) throws SftpException {
        try {
            Set<net.schmizz.sshj.sftp.OpenMode> openModes = SshjSftps.toSshjOpenModeSet(openMode);
            net.schmizz.sshj.sftp.FileAttributes attributes = SshjSftps.toFileAttributes(attrs);
            RemoteFile remoteFile = sftpClient.open(filepath, openModes, attributes);
            SshjSftpFile file = new SshjSftpFile(this, filepath);
            file.setRemoteFile(remoteFile);
            return file;
        } catch (Throwable ex) {
            throw SshjSftps.wrapSftpException(ex);
        }
    }

    @Override
    public void createSymlink(String src, String target) throws SftpException {
        try {
            sftpClient.symlink(src, target);
        } catch (Throwable ex) {
            throw SshjSftps.wrapSftpException(ex);
        }
    }

    @Override
    public String readLink(String path) throws SftpException {
        try {
            return sftpClient.readlink(path);
        } catch (Throwable ex) {
            throw SshjSftps.wrapSftpException(ex);
        }
    }

    @Override
    public String canonicalPath(String path) throws SftpException {
        try {
            return sftpClient.canonicalize(path);
        } catch (Throwable ex) {
            throw SshjSftps.wrapSftpException(ex);
        }
    }


    @Override
    public FileAttrs stat(String filepath) throws SftpException {
        try {
            net.schmizz.sshj.sftp.FileAttributes fileAttributes = sftpClient.stat(filepath);
            return SshjSftps.fromFileAttributes(fileAttributes);
        } catch (Throwable ex) {
            throw SshjSftps.wrapSftpException(ex);
        }
    }

    @Override
    public FileAttrs lstat(String filepath) throws SftpException {
        try {
            net.schmizz.sshj.sftp.FileAttributes fileAttributes = sftpClient.lstat(filepath);
            return SshjSftps.fromFileAttributes(fileAttributes);
        } catch (Throwable ex) {
            throw SshjSftps.wrapSftpException(ex);
        }
    }

    @Override
    public void setStat(String path, FileAttrs attrs) throws SftpException {
        try {
            sftpClient.setattr(path, SshjSftps.toFileAttributes(attrs));
        } catch (Throwable ex) {
            throw SshjSftps.wrapSftpException(ex);
        }
    }

    @Override
    protected List<SftpResourceInfo> doListFiles(String directory) throws SftpException {
        try {
            List<RemoteResourceInfo> list = sftpClient.ls(directory);
            return Pipeline.of(list)
                    .filter(new Predicate<RemoteResourceInfo>() {
                        @Override
                        public boolean test(RemoteResourceInfo remoteResourceInfo) {
                            return !".".equals(remoteResourceInfo.getName()) && !"..".equals(remoteResourceInfo.getName());
                        }
                    })
                    .map(new Function<RemoteResourceInfo, SftpResourceInfo>() {
                        @Override
                        public SftpResourceInfo apply(RemoteResourceInfo remoteResourceInfo) {
                            return SshjSftps.fromRemoteResourceInfo(remoteResourceInfo);
                        }
                    }).asList();
        } catch (Throwable ex) {
            throw SshjSftps.wrapSftpException(ex);
        }
    }


    @Override
    public void mkdir(String directory, FileAttrs attributes) throws SftpException {
        try {
            sftpClient.mkdirs(directory);
        } catch (Throwable ex) {
            throw SshjSftps.wrapSftpException(ex);
        }
    }

    @Override
    public void rmdir(String directory) throws SftpException {
        try {
            sftpClient.rmdir(directory);
        } catch (Throwable ex) {
            throw SshjSftps.wrapSftpException(ex);
        }
    }

    @Override
    public void rm(String filepath) throws SftpException {
        try {
            sftpClient.rm(filepath);
        } catch (Throwable ex) {
            throw SshjSftps.wrapSftpException(ex);
        }
    }

    @Override
    public void mv(String oldFilepath, String newFilepath) throws SftpException {
        try {
            sftpClient.rename(oldFilepath, newFilepath);
        } catch (Throwable ex) {
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
