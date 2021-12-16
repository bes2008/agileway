package com.jn.agileway.vfs.filter;

import com.jn.agileway.vfs.VfsException;
import org.apache.commons.vfs2.FileObject;

public class IsFileFilter implements FileObjectFilter {
    private boolean isFile;

    public IsFileFilter() {
        this(true);
    }

    public IsFileFilter(boolean isFile) {
        this.isFile = isFile;
    }

    @Override
    public boolean test(FileObject fileObject) {
        try {
            return fileObject.isFile() ? isFile : !isFile;
        } catch (Throwable ex) {
            throw new VfsException(ex);
        }
    }
}
