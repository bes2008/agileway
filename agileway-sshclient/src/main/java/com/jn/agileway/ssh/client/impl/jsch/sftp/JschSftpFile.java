package com.jn.agileway.ssh.client.impl.jsch.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jn.agileway.ssh.client.sftp.SftpFile;
import com.jn.agileway.ssh.client.sftp.SftpSession;
import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;
import com.jn.agileway.ssh.client.sftp.exception.SftpException;
import com.jn.langx.io.stream.ByteArrayOutputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class JschSftpFile extends SftpFile {

    public JschSftpFile(SftpSession session, String path) {
        super(session, path);
    }

    public JschSftpFile(SftpSession session, String path, String fileHandle) {
        super(session, path, fileHandle);
    }

    @Override
    public int read(long fileOffset, byte[] buffer, int bufferOffset, int length) throws IOException {
        ChannelSftp channel = ((JschSftpSession) this.session).getChannel();
        ByteArrayOutputStream stream = new ByteArrayOutputStream(length);
        try {
            channel.get(this.path, stream, null, ChannelSftp.RESUME, fileOffset);
            stream.write(buffer, bufferOffset, length);
            return length;
        } catch (Throwable ex) {
            throw new SftpException(ex);
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
