package com.jn.agileway.ssh.client.impl.synergy.sftp;

import com.jn.agileway.ssh.client.sftp.AbstractSftpSession;
import com.jn.agileway.ssh.client.sftp.SftpFile;
import com.jn.agileway.ssh.client.sftp.SftpResourceInfo;
import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;
import com.jn.agileway.ssh.client.sftp.exception.SftpException;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.sshtools.client.sftp.SftpClient;
import com.sshtools.common.sftp.SftpFileAttributes;

import java.io.IOException;
import java.util.List;

public class SynergySftpSession extends AbstractSftpSession {
    private SftpClient client;

    public SynergySftpSession(SftpClient client) {
        this.client = client;
    }

    @Override
    protected List<SftpResourceInfo> doListFiles(String directory) throws IOException {
        try {
            com.sshtools.client.sftp.SftpFile[] files = this.client.ls(directory);
            return Pipeline.of(files).map(new Function<com.sshtools.client.sftp.SftpFile, SftpResourceInfo>() {
                @Override
                public SftpResourceInfo apply(com.sshtools.client.sftp.SftpFile sftpFile) {
                    try {
                        SftpResourceInfo resourceInfo = new SftpResourceInfo(sftpFile.getAbsolutePath(), SynergySftps.fromSftpFileAttributes(sftpFile.getAttributes()));
                        return resourceInfo;
                    } catch (Throwable ex) {
                        throw new SftpException(ex);
                    }
                }
            }).asList();
        } catch (Throwable ex) {
            throw new SftpException(ex);
        }
    }

    @Override
    public int getProtocolVersion() throws SftpException {
        try {
            return this.client.getSubsystemChannel().getServerVersion();
        } catch (Throwable ex) {
            throw new SftpException(ex);
        }
    }

    @Override
    public SftpFile open(String filepath, int openMode, FileAttrs attrs) throws IOException {
        return null;
    }

    @Override
    public void createSymlink(String src, String target) throws IOException {
        try {
            this.client.getSubsystemChannel().createSymbolicLink(target, src);
        } catch (Throwable ex) {
            throw new SftpException(ex);
        }
    }

    @Override
    public String readLink(String path) throws IOException {
        try {
            return this.client.getSymbolicLinkTarget(path);
        } catch (Throwable ex) {
            throw new SftpException(ex);
        }
    }

    @Override
    public String canonicalPath(String path) throws IOException {
        try {
            return this.client.getAbsolutePath(path);
        } catch (Throwable ex) {
            throw new SftpException(ex);
        }
    }

    @Override
    public FileAttrs stat(String filepath) throws IOException {
        try {
            SftpFileAttributes attributes = this.client.stat(filepath);
            return SynergySftps.fromSftpFileAttributes(attributes);
        } catch (Throwable ex) {
            throw new SftpException(ex);
        }
    }

    @Override
    public FileAttrs lstat(String filepath) throws IOException {
        try {
            SftpFileAttributes attributes = this.client.stat(filepath);
            return SynergySftps.fromSftpFileAttributes(attributes);
        } catch (Throwable ex) {
            throw new SftpException(ex);
        }
    }

    @Override
    public void setStat(String path, FileAttrs attrs) throws IOException {

    }

    @Override
    public void mkdir(String directory, FileAttrs attrs) throws IOException {

    }

    @Override
    public void rmdir(String directory) throws IOException {
        try {
            this.client.getSubsystemChannel().removeDirectory(directory);
        } catch (Throwable ex) {
            throw new SftpException(ex);
        }
    }

    @Override
    public void rm(String filepath) throws IOException {
        try {
            this.client.rm(filepath);
        } catch (Throwable ex) {
            throw new SftpException(ex);
        }
    }

    @Override
    public void mv(String oldFilepath, String newFilepath) throws IOException {
        try {
            this.client.getSubsystemChannel().renameFile(oldFilepath, newFilepath);
        } catch (Throwable ex) {
            throw new SftpException(ex);
        }
    }

    @Override
    public void close() throws IOException {
        client.getSubsystemChannel().close();
    }
}
