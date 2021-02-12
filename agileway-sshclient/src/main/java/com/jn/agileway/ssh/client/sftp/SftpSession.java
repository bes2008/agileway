package com.jn.agileway.ssh.client.sftp;

import com.jn.agileway.ssh.client.SshConnection;
import com.jn.langx.annotation.Nullable;

import java.io.Closeable;
import java.io.IOException;

/**
 * 代表了一个成功连接的 sftp 会话
 * <p>
 * 提供的方法中，都会带有 协议本身要发起的 packet 的说明。
 * 其中： packet_type, req_id 分别代表了 packet 类别，请求 id，这两个字段，均由 底层实现库自行封装
 */
public interface SftpSession extends Closeable {
    SshConnection getSshConnection();

    int getProtocolVersion();

    /**
     * <pre>
     * packet:
     * |packet_type|req_id|path|openMode|file_attributes|
     * </p>
     * @param path     the file path
     * @param openMode {int} the open mode
     * @param attrs    the file attributes
     * @return the opened file
     */
    SftpFile open(String path, OpenMode openMode, @Nullable FileAttributes attrs);

    SftpFile open(String path, int openMode, @Nullable FileAttributes attrs);

    /**
     * <pre>
     *     packet:
     *      |packet_type|req_id|file_handle|
     * </pre>
     *
     * @see SftpFile#close()
     */
    void close(SftpFile file);

    /**
     * packet:
     * |packet_type|req_id|file_handle|file_offset|length
     * <p>
     * 从file的 fileOffset 开始读取，最大读取 length 个 byte, 读取后放到 buffer中，从 buffer的 offset 开始放。
     * 返回实际读取的数据。
     * 若返回 -1，则代表结束
     *
     * @see SftpFile#read(long, byte[], int, int)
     */
    int read(SftpFile file, long fileOffset, byte[] buffer, int bufferOffset, int length);

    /**
     * packet:
     * |packet_type|req_id|file_handle|length|bytes|
     * <p>
     * 从data 的 offset 开始，取出 length 个 byte，写入远程文件。
     * 写入远程文件时，从 fileOffset 开始。
     *
     * @see SftpFile#write(long, byte[], int, int)
     */
    void write(SftpFile file, long fileOffset, byte[] data, int offset, int length);

    /**
     * 关闭 sftp session
     *
     * @throws IOException
     */
    @Override
    void close() throws IOException;
}
