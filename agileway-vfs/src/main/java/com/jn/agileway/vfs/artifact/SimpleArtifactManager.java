package com.jn.agileway.vfs.artifact;

import com.jn.agileway.vfs.management.FileDigit;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;

import java.util.List;

public class SimpleArtifactManager extends AbstractArtifactManager {

    @Override
    public FileObject getArtifactFile(Artifact artifact) throws FileSystemException {
        String localPath = getRepository().getPath(artifact);
        return getFileSystemManager().resolveFile(localPath);
    }

    @Override
    public List<FileDigit> getDigits(Artifact artifact) {
        return getDigits(this.getRepository(), artifact);
    }
}
