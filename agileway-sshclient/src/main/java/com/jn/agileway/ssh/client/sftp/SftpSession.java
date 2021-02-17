package com.jn.agileway.ssh.client.sftp;

import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.function.Predicate;

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
     * @param attrs    the file attributes if create
     * @return the opened file
     */
    SftpFile open(String filepath, OpenMode openMode, @Nullable FileAttrs attrs) throws IOException;

    SftpFile open(String filepath, int openMode, @Nullable FileAttrs attrs) throws IOException;

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
     * Retrieve the file attributes of a file. This method
     * follows symbolic links on the server.
     *
     * @return a FileAttributes object.
     * @throws IOException
     * @see #lstat(String)
     */
    FileAttrs stat(String filepath) throws IOException;

    FileAttrs lstat(String filepath) throws IOException;

    FileAttrs fstat(SftpFile file) throws IOException;

    /**
     * Modify the attributes of a file. Used for operations such as changing
     * the ownership, permissions or access times, as well as for truncating a file.
     *
     * @param path  the file path
     * @param attrs A FileAttributes object. Specifies the modifications to be
     *              made to the attributes of the file. Empty fields will be ignored.
     * @throws IOException
     */
    void setStat(String path, FileAttrs attrs) throws IOException;

    /**
     * packet:
     * |packet_type|req_id|file_handle|
     *
     * @param directory
     * @return
     */
    List<SftpResourceInfo> listFiles(String directory, Predicate<SftpResourceInfo> predicate) throws IOException;

    /**
     * packet:
     * |packet_type|req_id|path|file_attributes_flags_mask|file_attributes|
     *
     * @param directory
     * @param attributes
     * @return
     */
    void mkdir(String directory, FileAttrs attributes) throws IOException;

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