package com.jn.agileway.vfs;

import org.apache.commons.vfs2.FileSystemManager;

public interface FileSystemManagerAware {
    FileSystemManager getFileSystemManager();
    void setFileSystemManager(FileSystemManager manager);
}
