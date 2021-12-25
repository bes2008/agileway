package com.jn.agileway.vfs.filter;

import org.apache.commons.vfs2.FileType;

public class IsFileFilter extends FileTypeFilter{
    public IsFileFilter() {
        super(FileType.FILE);
    }
}
