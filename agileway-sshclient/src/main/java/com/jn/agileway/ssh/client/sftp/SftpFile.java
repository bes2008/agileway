package com.jn.agileway.ssh.client.sftp;

import ch.ethz.ssh2.SFTPv3Client;
import com.jn.agileway.ssh.client.sftp.filter.SftpFileFilter;
import com.jn.agileway.ssh.client.sftp.filter.SftpFilenameFilter;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.NotEmpty;
import com.jn.langx.annotation.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * 代表一个打开的 file
 */
public abstract class SftpFile implements Closeable {
    @NotEmpty
    protected String path;

    @NonNull
    protected SftpSession session;
    /**
     * UTF8 格式的字符串，代表了 文件句柄
     */
    @Nullable
    protected String fileHandle;
    @Nullable
    protected InputStream inputStream;
    @Nullable
    protected OutputStream outputStream;

    protected boolean isClosed = false;

    SftpFile(SftpSession session, String path, InputStream inputStream, OutputStream outputStream) {
        this.session = session;
        this.path = path;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    /**
     * <pre>
     *     packet:
     *      |packet_type|req_id|file_handle|
     * </pre>
     */
    @Override
    public abstract void close();

    /**
     * packet:
     * |packet_type|req_id|file_handle|file_offset|length
     * <p>
     * 从file的 fileOffset 开始读取，最大读取 length 个 byte, 读取后放到 buffer中，从 buffer的 offset 开始放。
     * 返回实际读取的数据。
     * 若返回 -1，则代表结束
     */
    public abstract int read(long fileOffset, byte[] buffer, int bufferOffset, int length);

    /**
     * packet:
     * |packet_type|req_id|file_handle|length|bytes|
     * <p>
     * 从data 的 offset 开始，取出 length 个 byte，写入远程文件。
     * 写入远程文件时，从 fileOffset 开始。
     */
    public abstract void write(long fileOffset, byte[] data, int offset, int length);

    public abstract List<String> list();

    public abstract List<String> list(SftpFilenameFilter filter);

    public abstract List<SftpFile> listFiles();

    public abstract List<SftpFile> listFiles(SftpFilenameFilter filter);

    public abstract List<SftpFile> listFiles(SftpFileFilter filter);

    public abstract boolean exist();
}
