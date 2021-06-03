package com.jn.agileway.vfs.provider.sftp;

import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemConfigBuilder;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;

import java.io.File;

public class SftpFileSystemConfigBuilder extends FileSystemConfigBuilder {


    private static final String _PREFIX = SftpFileSystemConfigBuilder.class.getName();
    private static final SftpFileSystemConfigBuilder BUILDER = new SftpFileSystemConfigBuilder();
    private static final String COMPRESSION = _PREFIX + "COMPRESSION";
    private static final String HOST_KEY_CHECK_ASK = "ask";
    private static final String HOST_KEY_CHECK_NO = "no";
    private static final String HOST_KEY_CHECK_YES = "yes";
    private static final String IDENTITIES = _PREFIX + ".IDENTITIES";
    private static final String IDENTITY_REPOSITORY_FACTORY = _PREFIX + "IDENTITY_REPOSITORY_FACTORY";
    private static final String KNOWN_HOSTS = _PREFIX + ".KNOWN_HOSTS";
    private static final String PREFERRED_AUTHENTICATIONS = _PREFIX + ".PREFERRED_AUTHENTICATIONS";

    private static final String PROXY_HOST = _PREFIX + ".PROXY_HOST";
    private static final String PROXY_USER = _PREFIX + ".PROXY_USER";
    private static final String PROXY_OPTIONS = _PREFIX + ".PROXY_OPTIONS";
    private static final String PROXY_TYPE = _PREFIX + ".PROXY_TYPE";
    private static final String PROXY_PORT = _PREFIX + ".PROXY_PORT";
    private static final String PROXY_PASSWORD = _PREFIX + ".PROXY_PASSWORD";
    private static final String PROXY_COMMAND = _PREFIX + ".PROXY_COMMAND";

    private static final String STRICT_HOST_KEY_CHECKING = _PREFIX + ".STRICT_HOST_KEY_CHECKING";
    private static final String TIMEOUT = _PREFIX + ".TIMEOUT";
    private static final String USER_DIR_IS_ROOT = _PREFIX + ".USER_DIR_IS_ROOT";
    private static final String ENCODING = _PREFIX + ".ENCODING";

    private SftpFileSystemConfigBuilder() {
        super("sftp.");
    }

    /**
     * Gets the singleton builder.
     *
     * @return the singleton builder.
     */
    public static SftpFileSystemConfigBuilder getInstance() {
        return BUILDER;
    }

    /**
     * @param opts The FileSystem options.
     * @return The names of the compression algorithms, comma-separated.
     * @see #setCompression
     */
    public String getCompression(final FileSystemOptions opts) {
        return this.getString(opts, COMPRESSION);
    }

    @Override
    protected Class<? extends FileSystem> getConfigClass() {
        return SftpFileSystem.class;
    }

    /**
     * Gets the file name encoding.
     *
     * @param opts The FileSystem options.
     * @return the file name encoding
     */
    public String getFileNameEncoding(final FileSystemOptions opts) {
        return this.getString(opts, ENCODING);
    }

    /**
     * @param opts The FileSystem options.
     * @return the known hosts File.
     * @see #setKnownHosts
     */
    public File getKnownHosts(final FileSystemOptions opts) {
        return (File) this.getParam(opts, KNOWN_HOSTS);
    }

    /**
     * Gets authentication order.
     *
     * @param opts The FileSystem options.
     * @return The authentication order.
     * @since 2.0
     */
    public String getPreferredAuthentications(final FileSystemOptions opts) {
        return getString(opts, PREFERRED_AUTHENTICATIONS);
    }


    /**
     * Gets the user name for the proxy used for the SFTP connection.
     *
     * @param opts The FileSystem options.
     * @return proxyUser
     * @see #setProxyUser
     * @since 2.1
     */
    public String getProxyUser(final FileSystemOptions opts) {
        return this.getString(opts, PROXY_USER);
    }

    /**
     * @param opts The FileSystem options.
     * @return the option value The host key checking.
     * @see #setStrictHostKeyChecking(FileSystemOptions, String)
     */
    public String getStrictHostKeyChecking(final FileSystemOptions opts) {
        return this.getString(opts, STRICT_HOST_KEY_CHECKING, HOST_KEY_CHECK_NO);
    }

    /**
     * @param opts The FileSystem options.
     * @return The timeout value in milliseconds.
     * @see #setTimeout
     */
    public Integer getTimeout(final FileSystemOptions opts) {
        return this.getInteger(opts, TIMEOUT);
    }

    /**
     * Returns {@link Boolean#TRUE} if VFS should treat the user directory as the root directory. Defaults to
     * <code>Boolean.TRUE</code> if the method {@link #setUserDirIsRoot(FileSystemOptions, boolean)} has not been
     * invoked.
     *
     * @param opts The FileSystemOptions.
     * @return <code>Boolean.TRUE</code> if VFS treats the user directory as the root directory.
     * @see #setUserDirIsRoot
     */
    public Boolean getUserDirIsRoot(final FileSystemOptions opts) {
        return this.getBoolean(opts, USER_DIR_IS_ROOT, Boolean.TRUE);
    }

    /**
     * Configures the compression algorithms to use.
     * <p>
     * For example, use {@code "zlib,none"} to enable compression.
     * <p>
     * See the Jsch documentation (in particular the README file) for details.
     *
     * @param opts        The FileSystem options.
     * @param compression The names of the compression algorithms, comma-separated.
     * @throws FileSystemException if an error occurs.
     */
    public void setCompression(final FileSystemOptions opts, final String compression) throws FileSystemException {
        this.setParam(opts, COMPRESSION, compression);
    }

    /**
     * Sets the file name encoding.
     *
     * @param opts             The FileSystem options.
     * @param fileNameEncoding The name of the encoding to use for file names.
     */
    public void setFileNameEncoding(final FileSystemOptions opts, final String fileNameEncoding) {
        this.setParam(opts, ENCODING, fileNameEncoding);
    }


    /**
     * Sets the known_hosts file. e.g. {@code /home/user/.ssh/known_hosts2}.
     * <p>
     * We use {@link java.io.File} because JSch cannot deal with VFS FileObjects.
     *
     * @param opts       The FileSystem options.
     * @param knownHosts The known hosts file.
     * @throws FileSystemException if an error occurs.
     */
    public void setKnownHosts(final FileSystemOptions opts, final File knownHosts) throws FileSystemException {
        this.setParam(opts, KNOWN_HOSTS, knownHosts);
    }

    /**
     * Configures authentication order.
     *
     * @param opts                     The FileSystem options.
     * @param preferredAuthentications The authentication order.
     * @since 2.0
     */
    public void setPreferredAuthentications(final FileSystemOptions opts, final String preferredAuthentications) {
        this.setParam(opts, PREFERRED_AUTHENTICATIONS, preferredAuthentications);
    }


    /**
     * Sets the proxy username to use for the SFTP connection.
     *
     * @param opts      The FileSystem options.
     * @param proxyUser the username used to connect to the proxy
     * @see #getProxyUser
     * @since 2.1
     */
    public void setProxyUser(final FileSystemOptions opts, final String proxyUser) {
        this.setParam(opts, PROXY_USER, proxyUser);
    }

    /**
     * Configures the host key checking to use.
     * <p>
     * Valid arguments are: {@code "yes"}, {@code "no"} and {@code "ask"}.
     * </p>
     * <p>
     * See the jsch documentation for details.
     * </p>
     *
     * @param opts            The FileSystem options.
     * @param hostKeyChecking The host key checking to use.
     * @throws FileSystemException if an error occurs.
     */
    public void setStrictHostKeyChecking(final FileSystemOptions opts, final String hostKeyChecking)
            throws FileSystemException {
        if (hostKeyChecking == null || (!hostKeyChecking.equals(HOST_KEY_CHECK_ASK)
                && !hostKeyChecking.equals(HOST_KEY_CHECK_NO) && !hostKeyChecking.equals(HOST_KEY_CHECK_YES))) {
            throw new FileSystemException("vfs.provider.sftp/StrictHostKeyChecking-arg.error", hostKeyChecking);
        }

        this.setParam(opts, STRICT_HOST_KEY_CHECKING, hostKeyChecking);
    }

    /**
     * Sets the timeout value on Jsch session.
     *
     * @param opts    The FileSystem options.
     * @param timeout The timeout in milliseconds.
     */
    public void setTimeout(final FileSystemOptions opts, final Integer timeout) {
        this.setParam(opts, TIMEOUT, timeout);
    }

    /**
     * Sets the whether to use the user directory as root (do not change to file system root).
     *
     * @param opts          The FileSystem options.
     * @param userDirIsRoot true if the user directory is the root directory.
     */
    public void setUserDirIsRoot(final FileSystemOptions opts, final boolean userDirIsRoot) {
        this.setParam(opts, USER_DIR_IS_ROOT, userDirIsRoot ? Boolean.TRUE : Boolean.FALSE);
    }
}
