package com.jn.agileway.vfs.management;

public interface FileManagerAware<M extends FileManager> {
    M getManager();

    void setManager(M manager);
}
