package com.jn.agileway.vfs.provider.sftp;


import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.provider.FileNameParser;
import org.apache.commons.vfs2.provider.URLFileNameParser;

/**
 * Implementation for SFTP. Sets the default port to 22.
 */
public class SftpFileNameParser extends URLFileNameParser {
    private static final int DEFAULT_PORT = 22;

    private static final SftpFileNameParser INSTANCE = new SftpFileNameParser();

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

    @Override
    protected String extractHostName(StringBuilder name) {
        return super.extractHostName(name);
    }

    @Override
    protected int extractPort(StringBuilder name, String uri) throws FileSystemException {
        return super.extractPort(name, uri);
    }
}
