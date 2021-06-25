package com.jn.agileway.vfs.artifact;

import org.apache.commons.vfs2.FileSystemManager;

public abstract class AbstractArtifactManager implements ArtifactManager{
    private FileSystemManager fileSystemManager;

    @Override
    public FileSystemManager getFileSystemManager() {
        return fileSystemManager;
    }

    @Override
    public void setFileSystemManager(FileSystemManager fileSystemManager) {
        this.fileSystemManager = fileSystemManager;
    }
}
