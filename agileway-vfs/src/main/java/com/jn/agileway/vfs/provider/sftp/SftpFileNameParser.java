package com.jn.agileway.vfs.provider.sftp;


import org.apache.commons.vfs2.provider.FileNameParser;
import org.apache.commons.vfs2.provider.URLFileNameParser;

/**
 * Implementation for SFTP. Sets the default port to 22.
 */
public class SftpFileNameParser extends URLFileNameParser {
    private static final int DEFAULT_PORT = 22;

    private static final org.apache.commons.vfs2.provider.sftp.SftpFileNameParser INSTANCE = new org.apache.commons.vfs2.provider.sftp.SftpFileNameParser();

    /**
     * Creates a new instance with a the default port 22.
     */
    public SftpFileNameParser() {
        super(DEFAULT_PORT);
    }

    /**
     * Gets the singleton instance.
     *
     * @return the singleton instance.
     */
    public static FileNameParser getInstance() {
        return INSTANCE;
    }
}
