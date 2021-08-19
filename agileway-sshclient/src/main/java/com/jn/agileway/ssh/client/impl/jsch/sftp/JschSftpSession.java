package com.jn.agileway.ssh.client.impl.jsch.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;
import com.jn.agileway.ssh.client.sftp.*;
import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;
import com.jn.agileway.ssh.client.sftp.exception.NoSuchFileSftpException;
import com.jn.agileway.ssh.client.sftp.exception.SftpException;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Predicate;
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
        } catch (com.jcraft.jsch.SftpException ex) {
            throw JschSftps.wrapSftpException(ex);
        }
    }

    @Override
    protected List<SftpResourceInfo> doListFiles(final String directory) throws SftpException {
        try {
            Vector<ChannelSftp.LsEntry> vector = channel.ls(directory);
            return Pipeline.of(vector)
                    .filter(new Predicate<ChannelSftp.LsEntry>() {
                        @Override
                        public boolean test(ChannelSftp.LsEntry entry) {
                            return !".".equals(entry.getFilename()) && !"..".equals(entry.getFilename());
                        }
                    }).map(new Function<ChannelSftp.LsEntry, SftpResourceInfo>() {
                        @Override
                        public SftpResourceInfo apply(ChannelSftp.LsEntry entry) {
                            FileAttrs attrs = JschSftps.fromSftpATTRS(entry.getAttrs());
                            return new SftpResourceInfo(directory + "/" + entry.getFilename(), attrs);
                        }
                    }).asList();
        } catch (com.jcraft.jsch.SftpException ex) {
            throw JschSftps.wrapSftpException(ex);
        }
    }

    @Override
    public SftpFile open(String filepath, int openMode, FileAttrs attrs) throws SftpException {
        if (!Sftps.exists(this, filepath)) {
            if (OpenMode.isCreatable(openMode)) {
                // 创建文件
                try {
                    channel.put(new ByteArrayInputStream(new byte[0]), filepath, ChannelSftp.OVERWRITE);
                } catch (com.jcraft.jsch.SftpException ex) {
                    throw JschSftps.wrapSftpException(ex);
                }
            } else {
                throw new NoSuchFileSftpException(StringTemplates.formatWithPlaceholder("no such file: {}", filepath));
            }
        }
        return new JschSftpFile(this, filepath);
    }

    @Override
    public void createSymlink(String src, String target) throws SftpException {
        try {
            channel.symlink(src, target);
        } catch (com.jcraft.jsch.SftpException ex) {
            throw JschSftps.wrapSftpException(ex);
        }
    }

    @Override
    public String readLink(String path) throws SftpException {
        try {
            return channel.readlink(path);
        } catch (com.jcraft.jsch.SftpException ex) {
            throw JschSftps.wrapSftpException(ex);
        }
    }

    @Override
    public String canonicalPath(String path) throws SftpException {
        try {
            return channel.realpath(path);
        } catch (com.jcraft.jsch.SftpException ex) {
            throw JschSftps.wrapSftpException(ex);
        }
    }

    @Override
    public FileAttrs stat(String filepath) throws SftpException {
        try {
            SftpATTRS sftpATTRS = channel.stat(filepath);
            return JschSftps.fromSftpATTRS(sftpATTRS);
        } catch (com.jcraft.jsch.SftpException ex) {
            throw JschSftps.wrapSftpException(ex);
        }
    }

    @Override
    public FileAttrs lstat(String filepath) throws SftpException {
        try {
            SftpATTRS sftpATTRS = channel.lstat(filepath);
            return JschSftps.fromSftpATTRS(sftpATTRS);
        } catch (com.jcraft.jsch.SftpException ex) {
            throw JschSftps.wrapSftpException(ex);
        }
    }

    @Override
    public void setStat(String path, FileAttrs attrs) throws SftpException {
        try {
            SftpATTRS sftpATTRS = channel.lstat(path);
            sftpATTRS = JschSftps.toSftpATTRS(sftpATTRS, attrs);
            channel.setStat(path, sftpATTRS);
        } catch (com.jcraft.jsch.SftpException ex) {
            throw JschSftps.wrapSftpException(ex);
        }
    }

    @Override
    public void mkdir(String directory, FileAttrs attributes) throws SftpException {
        try {
            channel.mkdir(directory);
        } catch (com.jcraft.jsch.SftpException ex) {
            throw JschSftps.wrapSftpException(ex);
        }
    }

    @Override
    public void rmdir(String directory) throws SftpException {
        try {
            channel.rmdir(directory);
        } catch (com.jcraft.jsch.SftpException ex) {
            throw JschSftps.wrapSftpException(ex);
        }
    }

    @Override
    public void rm(String filepath) throws SftpException {
        try {
            channel.rm(filepath);
        } catch (com.jcraft.jsch.SftpException ex) {
            throw JschSftps.wrapSftpException(ex);
        }
    }

    @Override
    public void mv(String oldFilepath, String newFilepath) throws SftpException {
        try {
            channel.rename(oldFilepath, newFilepath);
        } catch (com.jcraft.jsch.SftpException ex) {
            throw JschSftps.wrapSftpException(ex);
        }
    }

    @Override
    public void close() throws IOException {
        channel.disconnect();
    }
}
