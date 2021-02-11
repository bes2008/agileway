package com.jn.agileway.ssh.client.sftp;

import com.jn.agileway.ssh.client.SshConnection;
import com.jn.langx.annotation.Nullable;

import java.io.Closeable;
import java.io.IOException;

public interface SftpSession extends Closeable {
    SshConnection getSshConnection();

    int getProtocolVersion();

    byte[] open(String path, OpenMode openMode,@Nullable FileAttributes attrs);

    @Override
    void close() throws IOException;
}
