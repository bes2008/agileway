package com.jn.agileway.vfs.provider.sftp;

import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.provider.AbstractOriginatingFileProvider;
import org.apache.commons.vfs2.provider.GenericFileName;

import java.util.Collection;

public class SftpProvider extends AbstractOriginatingFileProvider {
    @Override
    protected FileSystem doCreateFileSystem(FileName rootName, FileSystemOptions fileSystemOptions) throws FileSystemException {

        // Create the file system
        final GenericFileName root = (GenericFileName) rootName;



        return null;
    }

    @Override
    public Collection<Capability> getCapabilities() {
        return null;
    }
}
