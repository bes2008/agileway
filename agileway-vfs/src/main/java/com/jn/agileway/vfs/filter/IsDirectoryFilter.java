package com.jn.agileway.vfs.filter;

import org.apache.commons.vfs2.FileType;

public class IsDirectoryFilter extends FileTypeFilter {
    public IsDirectoryFilter() {
        super(FileType.FOLDER);
    }
}
