package com.jn.agileway.ssh.client.sftp;

import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.NotEmpty;

import java.io.Closeable;
import java.io.IOException;

/**
 * 代表一个打开的 file
 */
public abstract class SftpFile implements Closeable {
    @NotEmpty
    protected String path;

    @NonNull
    protected SftpSession session;

    protected boolean isClosed = false;

    public SftpFile(SftpSession session, String path) {
        this.session = session;
        this.path = path;
    }

    public SftpSession getSession(){
        return session;
    }

    /**
     * packet:
     * |packet_type|req_id|file_handle|file_offset|length
     * <p>
     * 从file的 fileOffset 开始读取，最大读取 length 个 byte, 读取后放到 buffer中，从 buffer的 offset 开始放。
     * 返回实际读取的数据长度。
     * 若返回 -1，则代表结束
     */
    public abstract int read(long fileOffset, byte[] buffer, int bufferOffset, int length) throws IOException;

    /**
     * packet:
     * |packet_type|req_id|file_handle|length|bytes|
     * <p>
     * 从data 的 offset 开始，取出 length 个 byte，写入远程文件。
     * 写入远程文件时，从 fileOffset 开始。
     */
    public abstract void write(long fileOffset, byte[] data, int offset, int length) throws IOException;

    /**
     * FSETSTAT
     *
     * @param attrs
     * @throws IOException
     */
    public abstract void setAttributes(FileAttrs attrs) throws IOException;

    /**
     * FSTAT
     *
     * @throws IOException
     */
    public abstract FileAttrs getAttributes() throws IOException;

}
