package com.jn.agileway.vfs.provider.sftp;

import com.jn.agileway.ssh.client.SshConnection;
import com.jn.agileway.ssh.client.SshConnectionConfig;
import com.jn.agileway.ssh.client.SshConnectionFactory;
import com.jn.agileway.ssh.client.SshConnectionFactoryRegistry;
import com.jn.agileway.ssh.client.sftp.SftpSession;
import com.jn.langx.util.Strings;
import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.provider.AbstractOriginatingFileProvider;
import org.apache.commons.vfs2.provider.GenericFileName;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class SftpFileProvider extends AbstractOriginatingFileProvider {

    public SftpFileProvider() {
        super();
        setFileNameParser(SftpFileNameParser.getInstance());
    }

    /**
     * The provider's capabilities.
     */
    protected static final Collection<Capability> capabilities = Collections.unmodifiableCollection(Arrays
            .asList(new Capability[]{Capability.CREATE, Capability.DELETE, Capability.RENAME, Capability.GET_TYPE,
                    Capability.LIST_CHILDREN, Capability.READ_CONTENT, Capability.URI, Capability.WRITE_CONTENT,
                    Capability.GET_LAST_MODIFIED, Capability.SET_LAST_MODIFIED_FILE, Capability.RANDOM_ACCESS_READ}));

    @Override
    protected FileSystem doCreateFileSystem(FileName rootName, FileSystemOptions fileSystemOptions) throws FileSystemException {

        // Create the file system
        final GenericFileName root = (GenericFileName) rootName;

        SshConnectionFactory sshConnectionFactory = new SshConnectionFactoryRegistry().getDefault();
        SshConnectionConfig connectionConfig = sshConnectionFactory.newConfig();

        connectionConfig.setHost(root.getHostName());
        connectionConfig.setPort(root.getPort());
        connectionConfig.setUser(root.getUserName());
        connectionConfig.setPassword(root.getPassword());


        SftpFileSystemConfigBuilder configBuilder = (SftpFileSystemConfigBuilder) getConfigBuilder();
        final File knownHostsFile = configBuilder.getKnownHosts(fileSystemOptions);
        if (knownHostsFile != null) {
            connectionConfig.setKnownHostsPath(knownHostsFile.getAbsolutePath());
        }


        // JSCH 特有属性：
        Integer timout = configBuilder.getTimeout(fileSystemOptions);
        if (timout != null) {
            connectionConfig.setProperty("ConnectTimeout", timout);
        }
        String strictHostKeyChecking = configBuilder.getStrictHostKeyChecking(fileSystemOptions);
        if (Strings.isNotEmpty(strictHostKeyChecking)) {
            connectionConfig.setProperty("StrictHostKeyChecking", strictHostKeyChecking);
        }

        final String preferredAuthentications = configBuilder.getPreferredAuthentications(fileSystemOptions);
        if (preferredAuthentications != null) {
            connectionConfig.setProperty("PreferredAuthentications", preferredAuthentications);
        }

        // set compression property
        final String compression = configBuilder.getCompression(fileSystemOptions);
        if (compression != null) {
            connectionConfig.setProperty("compression.s2c", compression);
            connectionConfig.setProperty("compression.c2s", compression);
        }


        SshConnection sshConnection = sshConnectionFactory.get(connectionConfig);
        SftpSession session = sshConnection.openSftpSession();

        return new SftpFileSystem(root, session, null, fileSystemOptions);
    }

    @Override
    public FileSystemConfigBuilder getConfigBuilder() {
        return SftpFileSystemConfigBuilder.getInstance();
    }

    @Override
    public Collection<Capability> getCapabilities() {
        return capabilities;
    }
}
