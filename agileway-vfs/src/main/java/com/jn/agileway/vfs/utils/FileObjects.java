package com.jn.agileway.vfs.utils;

import com.jn.agileway.vfs.VfsException;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Preconditions;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.Selectors;

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


}
