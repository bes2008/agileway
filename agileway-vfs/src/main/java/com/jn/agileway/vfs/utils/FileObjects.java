package com.jn.agileway.vfs.utils;

import com.jn.agileway.vfs.VfsException;
import com.jn.langx.annotation.Nullable;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;

public class FileObjects {
    private FileObjects(){}

    public static boolean isExists(@Nullable FileObject fileObject){
        if(fileObject==null){
            return false;
        }
        try {
            return fileObject.exists();
        }catch (FileSystemException fileSystemException){
            throw new VfsException(fileSystemException);
        }
    }

    public static String getFileName(FileObject fileObject){
        return fileObject.getName().getBaseName();
    }
}
