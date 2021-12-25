package com.jn.agileway.vfs.filter;

import org.apache.commons.vfs2.FileObject;

public class NotFileFilter implements FileObjectFilter {
    private FileObjectFilter filter;

    public NotFileFilter(FileObjectFilter filter) {
        this.filter = filter;
    }

    @Override
    public boolean test(FileObject fileObject) {
        return !filter.test(fileObject);
    }
}
