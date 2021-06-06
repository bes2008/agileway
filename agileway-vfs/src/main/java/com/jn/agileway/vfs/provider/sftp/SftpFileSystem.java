package com.jn.agileway.vfs.provider.sftp;

import com.jn.agileway.ssh.client.sftp.SftpSession;
import org.apache.commons.vfs2.Capability;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.AbstractFileName;
import org.apache.commons.vfs2.provider.AbstractFileSystem;

import java.util.Collection;

public class SftpFileSystem extends AbstractFileSystem {
    private SftpSession sftpSession;

    public SftpFileSystem(FileName rootName, SftpSession session, FileObject parentLayer, FileSystemOptions fileSystemOptions) {
        super(rootName, parentLayer, fileSystemOptions);
        this.sftpSession = session;
    }

    public SftpSession getSftpSession() {
        return this.sftpSession;
    }

    @Override
    protected FileObject createFile(AbstractFileName name) throws Exception {
        return new SftpFileObject(name, this);
    }

    @Override
    public double getLastModTimeAccuracy() {
        return 1000;
    }

    @Override
    protected void addCapabilities(Collection<Capability> caps) {
        caps.addAll(SftpFileProvider.capabilities);
    }
}
