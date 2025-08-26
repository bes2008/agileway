package com.jn.agileway.ssh.client.sftp;

import com.jn.agileway.ssh.client.SshConnection;
import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;
import com.jn.agileway.ssh.client.sftp.exception.SftpException;
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

    SshConnection getSshConnection();

    int getProtocolVersion() throws SftpException;

    /**
     * <pre>
     * packet:
     * |packet_type|req_id|path|openMode|file_attributes|
     *
     * packet_type: OPEN
     *
     * </p>
     * @param filepath     the file path
     * @param openMode {int} the open mode
     * @param attrs    the file attributes if create
     * @return the opened file
     */
    SftpFile open(String filepath, OpenMode openMode, @Nullable FileAttrs attrs) throws SftpException;

    SftpFile open(String filepath, int openMode, @Nullable FileAttrs attrs) throws SftpException;

    /**
     * Create a symbolic link on the server. Creates a link "src" that points
     * to "target".
     * <p>
     * packet_type: SYMLINK
     *
     * @throws IOException
     */
    void createSymlink(String src, String target) throws SftpException;

    /**
     * Read the target of a symbolic link.
     * packet_type: READLINK
     *
     * @return The target of the link.
     * @throws IOException
     */
    String readLink(String path) throws SftpException;

    /**
     * packet_type: REALPATH
     *
     * @param path
     * @throws IOException
     */
    String canonicalPath(String path) throws SftpException;


    /**
     * Retrieve the file attributes of a file. This method
     * follows symbolic links on the server.
     * <p>
     * packet_type: STAT
     *
     * @return a FileAttributes object.
     * @throws IOException
     * @see #lstat(String)
     */
    FileAttrs stat(String filepath) throws SftpException;

    /**
     * packet_type: LSTAT
     *
     * @param filepath
     * @return
     * @throws IOException
     */
    FileAttrs lstat(String filepath) throws SftpException;

    /**
     * Modify the attributes of a file. Used for operations such as changing
     * the ownership, permissions or access times, as well as for truncating a file.
     *
     * packet_type: SETSTAT
     *
     * @param path  the file path
     * @param attrs A FileAttributes object. Specifies the modifications to be
     *              made to the attributes of the file. Empty fields will be ignored.
     * @throws IOException
     */
    void setStat(String path, FileAttrs attrs) throws SftpException;

    List<SftpResourceInfo> listFiles(String directory) throws SftpException;

    /**
     * 列出目录直接子项
     *
     * packet:
     * |packet_type|req_id|file_handle|
     *
     * packet_type: READDIR
     *
     * @param directory
     * @return
     */
    List<SftpResourceInfo> listFiles(String directory, Predicate<SftpResourceInfo> predicate) throws SftpException;

    /**
     * 只创建一层目录
     *
     * packet:
     * |packet_type|req_id|path|file_attributes_flags_mask|file_attributes|
     *
     * packet_type: MKDIR
     *
     * @param directory
     * @param attributes
     */
    void mkdir(String directory, FileAttrs attributes) throws SftpException;

    /**
     * 递归创建目录
     * <p>
     * packet:
     * |packet_type|req_id|path|file_attributes_flags_mask|file_attributes|
     * <p>
     * packet_type: MKDIR
     *
     * @param directory
     * @param attributes
     */
    void mkdirs(String directory, FileAttrs attributes) throws SftpException;
    /**
     * 移除一个空目录
     *
     * packet:
     * |packet_type|req_id|path|
     *
     * packet_type: RMDIR
     *
     * 只能删除空目录，目录下有内容时，需要先清理目录里的内容
     * @param directory
     * @return
     *
     * @see Sftps#removeDir(SftpSession, String, boolean)
     */
    void rmdir(String directory) throws SftpException;

    /**
     * 移除一个文件
     *
     * packet:
     * |packet_type|req_id|path|
     *
     * @param filepath
     */
    void rm(String filepath) throws SftpException;

    /**
     * 移动（重命名）一个文件
     *
     * packet:
     * |packet_type|req_id|path|
     *
     * packet_type: RM
     *
     * @param oldFilepath
     * @param newFilepath
     */
    void mv(String oldFilepath, String newFilepath) throws SftpException;

    /**
     * 关闭 sftp session
     *
     * @throws IOException
     */
    @Override
    void close() throws IOException;
}