package com.jn.agileway.ssh.client.sftp;

import com.jn.langx.util.Preconditions;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class SftpFileOutputStream extends OutputStream {

    private final SftpFile sftpFile;
    private int filePosition = 0;
    private final ByteBuffer byteBuffer;

    public SftpFileOutputStream(SftpFile sftpFile, int bufferSize) {
        Preconditions.checkNotNull(sftpFile);
        this.sftpFile = sftpFile;
        Preconditions.checkTrue(bufferSize > 0);
        this.byteBuffer = ByteBuffer.allocate(bufferSize);
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
    }
}
