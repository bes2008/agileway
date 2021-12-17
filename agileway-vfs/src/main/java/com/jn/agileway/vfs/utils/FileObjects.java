package com.jn.agileway.vfs.utils;

import com.jn.agileway.vfs.VfsException;
import com.jn.agileway.vfs.filter.FileObjectFilter;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.provider.local.LocalFile;

import java.io.File;
import java.util.List;

public class FileObjects {
    private FileObjects() {
    }

    public static boolean isExists(@Nullable FileObject fileObject) {
        if (fileObject == null) {
            return false;
        }
        try {
            return fileObject.exists();
        } catch (FileSystemException fileSystemException) {
            throw new VfsException(fileSystemException);
        }
    }

    public static String getFileName(FileObject fileObject) {
        return fileObject.getName().getBaseName();
    }

    public static boolean delete(FileObject fileObject) {
        Preconditions.checkNotNull(fileObject);
        if (isExists(fileObject)) {
            try {
                if (fileObject.isFile()) {
                    fileObject.delete(Selectors.SELECT_SELF);

                } else if (fileObject.isFolder()) {
                    fileObject.delete(Selectors.SELECT_ALL);
                }
            } catch (FileSystemException ex) {
                if (!isExists(fileObject)) {
                    return true;
                }
                throw new VfsException(ex);
            }
        }
        return true;
    }

    public static List<FileObject> findChildren(FileObject directory, FileObjectFilter filter) {
        try {
            if (isExists(directory) && directory.isFolder()) {
                FileObject[] children = directory.getChildren();
                return Pipeline.of(children).filter(filter).asList();
            }
            return null;
        } catch (FileSystemException ex) {
            throw new VfsException(ex.getMessage());
        }
    }

}
