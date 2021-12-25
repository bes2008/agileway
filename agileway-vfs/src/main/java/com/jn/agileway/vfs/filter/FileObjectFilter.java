package com.jn.agileway.vfs.filter;

import com.jn.langx.util.function.Predicate;
import org.apache.commons.vfs2.FileObject;

public interface FileObjectFilter extends Predicate<FileObject> {
    @Override
    public boolean test(FileObject fileObject);
}
