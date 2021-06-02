package com.jn.agileway.ssh.client.sftp;

import com.jn.langx.util.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class SftpFileInputStream extends InputStream {

    private static final int DEFAULT_BUFFER_SIZE = 8192;

    private final SftpFile sftpFile;
    private long filePosition = 0;
    private transient final ByteBuffer byteBuffer;

    public SftpFileInputStream(SftpFile sftpFile) {
        this(sftpFile, DEFAULT_BUFFER_SIZE);
    }

    public SftpFileInputStream(SftpFile sftpFile, int buffSize) {
        this(sftpFile, buffSize, 0);
    }

    public SftpFileInputStream(SftpFile sftpFile, int buffSize, long filePosition) {
        Preconditions.checkNotNull(sftpFile);
        this.sftpFile = sftpFile;
        Preconditions.checkTrue(buffSize > 0);
        this.byteBuffer = ByteBuffer.allocate(buffSize);
        Preconditions.checkTrue(filePosition >= 0);
        this.filePosition = filePosition;
    }

    private int doRead() throws IOException {
        int length = sftpFile.read(filePosition, byteBuffer.array(), 0, this.byteBuffer.capacity());
        if (length != -1) {
            filePosition = filePosition + length;
            this.byteBuffer.position(0);
            this.byteBuffer.limit(length);
        }
        return length;
    }

    @Override
    public int read() throws IOException {
        if (!byteBuffer.hasRemaining()) {
            int length = doRead();
            if (length == -1) {
                return -1;
            }
        }
        return byteBuffer.get();
    }


    @Override
    public int available() throws IOException {
        return (int) (sftpFile.getAttributes().getSize() - filePosition);
    }

    @Override
    public void close() throws IOException {
        sftpFile.close();
    }

}
