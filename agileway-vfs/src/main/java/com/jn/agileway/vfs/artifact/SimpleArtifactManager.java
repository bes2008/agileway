package com.jn.agileway.vfs.artifact;

import com.jn.langx.annotation.NonNull;
import com.jn.agileway.vfs.artifact.repository.ArtifactRepository;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;

public class SimpleArtifactManager extends AbstractArtifactManager {
    @NonNull
    private ArtifactRepository repository;

    public ArtifactRepository getRepository() {
        return repository;
    }

    public void setRepository(ArtifactRepository repository) {
        this.repository = repository;
    }

    @Override
    public FileObject getArtifactFile(Artifact artifact) throws FileSystemException {
        String localPath = repository.getPath(artifact);
        return getFileSystemManager().resolveFile(localPath);
    }
}
