package com.jn.agileway.vfs.artifact.repository;

public interface ArtifactRepositoryAware {
    ArtifactRepository getRepository();

    void setRepository(ArtifactRepository repository);
}
