package com.jn.agileway.vfs.filter;

import com.jn.agileway.vfs.utils.FileObjects;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileType;

public class FileTypeFilter implements FileObjectFilter{
    private FileType expected;

    public FileTypeFilter(FileType fileType){
        this.expected = fileType;
    }

    @Override
    public boolean test(FileObject fileObject) {
        if(FileObjects.isExists(fileObject)) {
            try {
                return fileObject.getType() == expected;
            }catch (Throwable ex){
                // ignore it
            }
        }
        return false;
    }
}
