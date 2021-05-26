package com.jn.agileway.vfs.provider.sftp;

import org.apache.commons.vfs2.Capability;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.AbstractFileName;
import org.apache.commons.vfs2.provider.AbstractFileSystem;

import java.util.Collection;

public class SftpFileSystem extends AbstractFileSystem {

    public SftpFileSystem(FileName rootName, FileObject parentLayer, FileSystemOptions fileSystemOptions) {
        super(rootName, parentLayer, fileSystemOptions);
    }

    @Override
    protected FileObject createFile(AbstractFileName name) throws Exception {
        return null;
    }

    @Override
    protected void addCapabilities(Collection<Capability> caps) {

    }
}
