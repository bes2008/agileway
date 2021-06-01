package com.jn.agileway.vfs.provider.sftp;

import com.jn.agileway.ssh.client.SshConnection;
import com.jn.agileway.ssh.client.SshConnectionConfig;
import com.jn.agileway.ssh.client.SshConnectionFactory;
import com.jn.agileway.ssh.client.SshConnectionFactoryRegistry;
import com.jn.agileway.ssh.client.sftp.SftpSession;
import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.provider.AbstractOriginatingFileProvider;
import org.apache.commons.vfs2.provider.GenericFileName;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class SftpProvider extends AbstractOriginatingFileProvider {


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
        SshConnection sshConnection = sshConnectionFactory.get(connectionConfig);
        SftpSession session = sshConnection.openSftpSession();

        return new SftpFileSystem(root, session, null, fileSystemOptions);
    }

    @Override
    public Collection<Capability> getCapabilities() {
        return capabilities;
    }
}
