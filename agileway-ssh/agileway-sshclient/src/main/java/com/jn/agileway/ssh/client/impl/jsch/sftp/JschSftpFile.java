package com.jn.agileway.ssh.client.impl.jsch.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jn.agileway.ssh.client.sftp.SftpFile;
import com.jn.agileway.ssh.client.sftp.SftpSession;
import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;
import com.jn.agileway.ssh.client.sftp.exception.SftpException;
import com.jn.langx.util.io.IOs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class JschSftpFile extends SftpFile {

    public JschSftpFile(SftpSession session, String path) {
        super(session, path);
    }

    @Override
    public int read(long fileOffset, byte[] buffer, int bufferOffset, int length) throws IOException {
        ChannelSftp channel = ((JschSftpSession) this.session).getChannel();
        InputStream inputStream = null;
        try {
            inputStream = channel.get(this.path, null, fileOffset);
            int readLength = inputStream.read(buffer, bufferOffset, length);
            return readLength;
        } catch (Throwable ex) {
            throw new SftpException(ex);
        } finally {
            IOs.close(inputStream);
        }
    }

    @Override
    public void write(long fileOffset, byte[] data, int offset, int length) throws IOException {
        ChannelSftp channel = ((JschSftpSession) this.session).getChannel();
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (offset >= data.length) {
            throw new IllegalArgumentException("bufferOffset >= buffer.length");
        }

        if (length == 0) {
            return;
        }
        if (offset + length > data.length) {
            length = data.length - offset;
        }
        byte[] buffer = new byte[length];
        System.arraycopy(data, offset, buffer, 0, length);
        try {
            channel.put(new ByteArrayInputStream(buffer), this.path, fileOffset == 0 ? ChannelSftp.OVERWRITE : ChannelSftp.APPEND);
        } catch (Throwable ex) {
            throw new SftpException(ex);
        }
    }

    @Override
    public void setAttributes(FileAttrs attrs) throws IOException {
        session.setStat(this.path, attrs);
    }

    @Override
    public FileAttrs getAttributes() throws IOException {
        return session.stat(this.path);
    }

    @Override
    public void close() throws IOException {
        this.isClosed = true;
    }
}
