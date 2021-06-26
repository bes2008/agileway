package com.jn.agileway.vfs.artifact;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;

import java.util.List;

public interface ArtifactManager {
    void setFileSystemManager(FileSystemManager fileSystemManager);

    FileSystemManager getFileSystemManager();

    FileObject getArtifactFile(Artifact artifact) throws FileSystemException;

    /**
     * @param artifact
     * @return
     */
    List<ArtifactDigit> getDigits(Artifact artifact);
}
