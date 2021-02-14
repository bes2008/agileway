package com.jn.agileway.ssh.client.sftp;

import com.jn.agileway.ssh.client.sftp.attrs.FileAttributes;
import com.jn.agileway.ssh.client.sftp.filter.SftpFileFilter;
import com.jn.langx.annotation.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

/**
 * 代表了一个成功连接的 sftp 会话
 * <p>
 * 提供的方法中，都会带有 协议本身要发起的 packet 的说明。
 * 其中： packet_type, req_id 分别代表了 packet 类别，请求 id，这两个字段，均由 底层实现库自行封装
 */
public interface SftpSession extends Closeable {

    int getProtocolVersion();

    /**
     * <pre>
     * packet:
     * |packet_type|req_id|path|openMode|file_attributes|
     * </p>
     * @param filepath     the file path
     * @param openMode {int} the open mode
     * @param attrs    the file attributes
     * @return the opened file
     */
    SftpFile open(String filepath, OpenMode openMode, @Nullable FileAttributes attrs);

    SftpFile open(String filepath, int openMode, @Nullable FileAttributes attrs);

    /**
     * Create a symbolic link on the server. Creates a link "src" that points
     * to "target".
     *
     * @throws IOException
     */
    void createSymlink(String src, String target) throws IOException;

    /**
     * Read the target of a symbolic link.
     *
     * @return The target of the link.
     * @throws IOException
     */
    String readLink(String path) throws IOException;

    String canonicalPath(String path) throws IOException;

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
     * Retrieve the file attributes of a file. This method
     * follows symbolic links on the server.
     *
     * @return a FileAttributes object.
     * @throws IOException
     * @see #lstat(String)
     */
    FileAttributes stat(String filepath) throws IOException;

    FileAttributes lstat(String filepath) throws IOException;

    FileAttributes fstat(SftpFile file) throws IOException;

    /**
     * Modify the attributes of a file. Used for operations such as changing
     * the ownership, permissions or access times, as well as for truncating a file.
     *
     * @param path  the file path
     * @param attrs A FileAttributes object. Specifies the modifications to be
     *              made to the attributes of the file. Empty fields will be ignored.
     * @throws IOException
     */
    void setStat(String path, FileAttributes attrs) throws IOException;

    /**
     * packet:
     * |packet_type|req_id|file_handle|
     *
     * @param file
     * @return
     */
    List<SftpFile> listFiles(SftpFile file, SftpFileFilter filter) throws IOException;

    /**
     * packet:
     * |packet_type|req_id|path|file_attributes_flags_mask|file_attributes|
     *
     * @param directory
     * @param attributes
     * @return
     */
    void mkdir(String directory, FileAttributes attributes) throws IOException;

    /**
     * packet:
     * |packet_type|req_id|path|
     *
     * @param directory
     * @return
     */
    void rmdir(String directory) throws IOException;

    /**
     * packet:
     * |packet_type|req_id|path|
     *
     * @param filepath
     * @return
     */
    void rm(String filepath) throws IOException;

    /**
     * packet:
     * |packet_type|req_id|path|
     *
     * @param oldFilepath
     * @param newFilepath
     * @return
     */
    void mv(String oldFilepath, String newFilepath) throws IOException;

    /**
     * 关闭 sftp session
     *
     * @throws IOException
     */
    @Override
    void close() throws IOException;
}