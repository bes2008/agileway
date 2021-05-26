package com.jn.agileway.vfs.provider.sftp;

import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.provider.AbstractFileName;
import org.apache.commons.vfs2.provider.AbstractFileObject;

import java.io.InputStream;

public class SftpFileObject extends AbstractFileObject<SftpFileSystem> {

    public SftpFileObject(AbstractFileName name, SftpFileSystem fs) {
        super(name, fs);
    }

    @Override
    protected long doGetContentSize() throws Exception {
        return 0;
    }

    @Override
    protected InputStream doGetInputStream() throws Exception {
        return null;
    }

    @Override
    protected FileType doGetType() throws Exception {
        return null;
    }

    @Override
    protected String[] doListChildren() throws Exception {
        return new String[0];
    }
}
