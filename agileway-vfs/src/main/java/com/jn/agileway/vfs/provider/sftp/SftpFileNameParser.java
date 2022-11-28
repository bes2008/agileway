package com.jn.agileway.vfs.provider.sftp;


import com.jn.langx.util.Strings;
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
        String nameStr = name.toString();
        int pathSeparatorIndex = Strings.indexOf(nameStr, "/");
        String hostAndPort = pathSeparatorIndex < 0 ? nameStr : nameStr.substring(0, pathSeparatorIndex);
        int portColonIndex = Strings.lastIndexOf(hostAndPort, ":", hostAndPort.length());
        String host = null;
        if (portColonIndex < 0) {
            host = hostAndPort;
        } else {
            String portString = Strings.substring(hostAndPort, portColonIndex + 1);
            boolean isPort = Strings.isNumeric(portString);
            if (isPort) {
                host = Strings.substring(hostAndPort, 0, portColonIndex);
            } else {
                host = hostAndPort;
            }
        }

        name.delete(0, host.length());
        return host;
    }

}
