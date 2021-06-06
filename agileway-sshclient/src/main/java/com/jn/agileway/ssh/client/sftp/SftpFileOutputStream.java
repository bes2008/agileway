package com.jn.agileway.ssh.client.sftp;

import com.jn.langx.util.Preconditions;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class SftpFileOutputStream extends OutputStream {
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private final SftpFile sftpFile;
    private long filePosition = 0;
    private final ByteBuffer byteBuffer;

    public SftpFileOutputStream(SftpFile sftpFile){
        this(sftpFile,DEFAULT_BUFFER_SIZE);
    }

    public SftpFileOutputStream(SftpFile sftpFile, int bufferSize){
        this(sftpFile, bufferSize, 0);
    }


    public SftpFileOutputStream(SftpFile sftpFile, int bufferSize, long filePosition) {
        Preconditions.checkNotNull(sftpFile);
        this.sftpFile = sftpFile;
        if(bufferSize<=0){
            bufferSize = DEFAULT_BUFFER_SIZE;
        }
        this.byteBuffer = ByteBuffer.allocate(bufferSize);
        Preconditions.checkTrue(filePosition>=0);
        this.filePosition = filePosition;
    }

    @Override
    public void write(int b) throws IOException {
        if (!byteBuffer.hasRemaining()) {
            doFlush();
        }
        byteBuffer.put((byte) b);
    }

    private void doFlush() throws IOException {
        byteBuffer.flip();
        sftpFile.write(filePosition, byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
        filePosition = filePosition + byteBuffer.limit();
        byteBuffer.clear();
    }

    @Override
    public void flush() throws IOException {
        doFlush();
    }

    @Override
    public void close() throws IOException {
        flush();
        sftpFile.close();
    }
}
