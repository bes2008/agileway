package com.jn.agileway.vfs.artifact;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.provider.AbstractFileObject;

import java.util.List;

public interface ArtifactManager {
    void setFileSystemManager(FileSystemManager fileSystemManager);

    FileSystemManager getFileSystemManager();

    AbstractFileObject getArtifactFile(Artifact artifact) throws FileSystemException;

    /**
     * @param artifact
     * @return
     */
    List<ArtifactDigit> getDigits(Artifact artifact);
}
