package com.jn.agileway.ssh.client.impl.jsch.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;
import com.jn.agileway.ssh.client.sftp.*;
import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;
import com.jn.agileway.ssh.client.sftp.exception.NoSuchFileSftpException;
import com.jn.agileway.ssh.client.sftp.exception.SftpException;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.collection.Collects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

public class JschSftpSession extends AbstractSftpSession {
    private static final Logger logger = LoggerFactory.getLogger(JschSftpSession.class);
    private ChannelSftp channel;

    public JschSftpSession(ChannelSftp channel) {
        this.channel = channel;
    }

    public ChannelSftp getChannel() {
        return this.channel;
    }

    @Override
    public int getProtocolVersion() throws SftpException {
        try {
            return channel.getServerVersion();
        } catch (Throwable ex) {
            throw new SftpException(ex);
        }
    }

    @Override
    protected List<SftpResourceInfo> doListFiles(String directory) throws IOException {
        try {
            Vector vector = channel.ls(directory);
            return Collects.asList(vector);
        } catch (com.jcraft.jsch.SftpException ex) {
            throw new SftpException(ex);
        }
    }

    @Override
    public SftpFile open(String filepath, int openMode, FileAttrs attrs) throws IOException {
        if (!Sftps.exist(this, filepath)) {
            if (OpenMode.isCreatable(openMode)) {
                // 创建文件
                try {
                    channel.put(new ByteArrayInputStream(new byte[0]), filepath, ChannelSftp.OVERWRITE);
                } catch (Throwable ex) {
                    throw new NoSuchFileSftpException(StringTemplates.formatWithPlaceholder("no such file: {}", filepath));
                }
            } else {
                throw new NoSuchFileSftpException(StringTemplates.formatWithPlaceholder("no such file: {}", filepath));
            }
        }
        return new JschSftpFile(this, filepath);
    }

    @Override
    public void createSymlink(String src, String target) throws IOException {
        try {
            channel.symlink(src, target);
        } catch (com.jcraft.jsch.SftpException ex) {
            throw new SftpException(ex);
        }
    }

    @Override
    public String readLink(String path) throws IOException {
        try {
            return channel.readlink(path);
        } catch (com.jcraft.jsch.SftpException ex) {
            throw new SftpException(ex);
        }
    }

    @Override
    public String canonicalPath(String path) throws IOException {
        try {
            return channel.realpath(path);
        } catch (com.jcraft.jsch.SftpException ex) {
            throw new SftpException(ex);
        }
    }

    @Override
    public FileAttrs stat(String filepath) throws IOException {
        try {
            SftpATTRS sftpATTRS = channel.stat(filepath);
            return JschSftps.fromSftpATTRS(sftpATTRS);
        } catch (com.jcraft.jsch.SftpException ex) {
            throw new SftpException(ex);
        }
    }

    @Override
    public FileAttrs lstat(String filepath) throws IOException {
        try {
            SftpATTRS sftpATTRS = channel.lstat(filepath);
            return JschSftps.fromSftpATTRS(sftpATTRS);
        } catch (com.jcraft.jsch.SftpException ex) {
            throw new SftpException(ex);
        }
    }

    @Override
    public void setStat(String path, FileAttrs attrs) throws IOException {
        try {
            SftpATTRS sftpATTRS = channel.lstat(path);
            sftpATTRS = JschSftps.toSftpATTRS(sftpATTRS, attrs);
            channel.setStat(path, sftpATTRS);
        } catch (com.jcraft.jsch.SftpException ex) {
            throw new SftpException(ex);
        }
    }

    @Override
    public void mkdir(String directory, FileAttrs attributes) throws IOException {
        try {
            channel.mkdir(directory);
        } catch (com.jcraft.jsch.SftpException ex) {
            throw new SftpException(ex);
        }
    }

    @Override
    public void rmdir(String directory) throws IOException {
        try {
            channel.rmdir(directory);
        } catch (com.jcraft.jsch.SftpException ex) {
            throw new SftpException(ex);
        }
    }

    @Override
    public void rm(String filepath) throws IOException {
        try {
            channel.rm(filepath);
        } catch (com.jcraft.jsch.SftpException ex) {
            throw new SftpException(ex);
        }
    }

    @Override
    public void mv(String oldFilepath, String newFilepath) throws IOException {
        try {
            channel.rename(oldFilepath, newFilepath);
        } catch (com.jcraft.jsch.SftpException ex) {
            throw new SftpException(ex);
        }
    }

    @Override
    public void close() throws IOException {
        channel.disconnect();
    }
}
