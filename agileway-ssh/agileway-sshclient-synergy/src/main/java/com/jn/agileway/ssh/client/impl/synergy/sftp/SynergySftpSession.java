package com.jn.agileway.ssh.client.impl.synergy.sftp;

import com.jn.agileway.ssh.client.sftp.*;
import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;
import com.jn.agileway.ssh.client.sftp.exception.NoSuchFileSftpException;
import com.jn.agileway.ssh.client.sftp.exception.SftpException;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Predicate;
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
    protected List<SftpResourceInfo> doListFiles(String directory) throws SftpException {
        try {
            com.sshtools.client.sftp.SftpFile[] files = this.client.ls(directory);
            return Pipeline.of(files)
                    .filter(new Predicate<com.sshtools.client.sftp.SftpFile>() {
                        @Override
                        public boolean test(com.sshtools.client.sftp.SftpFile sftpFile) {
                            return !".".equals(sftpFile.getFilename()) && !"..".equals(sftpFile.getFilename());
                        }
                    })
                    .map(new Function<com.sshtools.client.sftp.SftpFile, SftpResourceInfo>() {
                        @Override
                        public SftpResourceInfo apply(com.sshtools.client.sftp.SftpFile sftpFile) {
                            try {
                                SftpResourceInfo resourceInfo = new SftpResourceInfo(sftpFile.getAbsolutePath(), SynergySftps.fromSftpFileAttributes(sftpFile.attributes()));
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
    public SftpFile open(String filepath, int openMode, FileAttrs attrs) throws SftpException {
        try {
            com.sshtools.client.sftp.SftpHandle sf = null;

            if (!Sftps.exists(this, filepath)) {
                if (OpenMode.isCreatable(openMode)) {
                    sf = this.client.getSubsystemChannel().openFile(filepath, openMode);
                }
            } else {
                sf = this.client.getSubsystemChannel().openFile(filepath, openMode);
            }
            return new SynergySftpFile(this, filepath, sf);
        } catch (Throwable ex) {
            throw new SftpException(ex);
        }
    }

    @Override
    public void createSymlink(String src, String target) throws SftpException {
        try {
            this.client.getSubsystemChannel().createSymbolicLink(target, src);
        } catch (Throwable ex) {
            throw new SftpException(ex);
        }
    }

    @Override
    public String readLink(String path) throws SftpException {
        try {
            return this.client.getSymbolicLinkTarget(path);
        } catch (Throwable ex) {
            throw new SftpException(ex);
        }
    }

    @Override
    public String canonicalPath(String path) throws SftpException {
        try {
            return this.client.getAbsolutePath(path);
        } catch (Throwable ex) {
            throw new SftpException(ex);
        }
    }

    @Override
    public FileAttrs stat(String filepath) throws SftpException {
        try {
            SftpFileAttributes attributes = this.client.stat(filepath);
            return SynergySftps.fromSftpFileAttributes(attributes);
        } catch (Throwable ex) {
            if ("No such file".equals(ex.getMessage())) {
                throw new NoSuchFileSftpException(ex);
            }
            throw new SftpException(ex);

        }
    }

    @Override
    public FileAttrs lstat(String filepath) throws SftpException {
        try {
            SftpFileAttributes attributes = this.client.stat(filepath);
            return SynergySftps.fromSftpFileAttributes(attributes);
        } catch (Throwable ex) {
            throw new SftpException(ex);
        }
    }

    @Override
    public void setStat(String path, FileAttrs attrs) throws SftpException {
        if (attrs != null && Strings.isNotBlank(path)) {
            try {
                this.client.getSubsystemChannel().setAttributes(path, SynergySftps.toSftpFileAttributes(attrs, this.client.getSubsystemChannel().getCharsetEncoding()));
            } catch (Throwable ex) {
                throw new SftpException(ex);
            }
        }
    }

    @Override
    public void mkdir(String directory, FileAttrs attrs) throws SftpException {
        try {
            if (attrs != null) {
                this.client.getSubsystemChannel().makeDirectory(directory, SynergySftps.toSftpFileAttributes(attrs, this.client.getSubsystemChannel().getCharsetEncoding()));
            } else {
                this.client.getSubsystemChannel().makeDirectory(directory);
            }
        } catch (Throwable ex) {
            throw new SftpException(ex);
        }
    }

    @Override
    public void rmdir(String directory) throws SftpException {
        try {
            this.client.getSubsystemChannel().removeDirectory(directory);
        } catch (Throwable ex) {
            throw new SftpException(ex);
        }
    }

    @Override
    public void rm(String filepath) throws SftpException {
        try {
            this.client.rm(filepath);
        } catch (Throwable ex) {
            throw new SftpException(ex);
        }
    }

    @Override
    public void mv(String oldFilepath, String newFilepath) throws SftpException {
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
