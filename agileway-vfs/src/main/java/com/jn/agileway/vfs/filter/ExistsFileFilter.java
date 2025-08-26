package com.jn.agileway.vfs.filter;

import com.jn.agileway.vfs.utils.FileObjects;
import org.apache.commons.vfs2.FileObject;

public class ExistsFileFilter implements FileObjectFilter {
    private boolean exists;

    public ExistsFileFilter() {
        this(true);
    }

    public ExistsFileFilter(boolean exists) {
        this.exists = exists;
    }

    @Override
    public boolean test(FileObject fileObject) {
        return FileObjects.isExists(fileObject) == exists;
    }
}
