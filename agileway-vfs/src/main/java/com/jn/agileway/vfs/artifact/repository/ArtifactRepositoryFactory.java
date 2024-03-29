package com.jn.agileway.vfs.artifact.repository;

import com.jn.langx.registry.Registry;

public interface ArtifactRepositoryFactory {
    void setArtifactRepositoryLayoutRegistry(Registry<String, ArtifactRepositoryLayout> registry);

    Registry<String, ArtifactRepositoryLayout> getLayoutRegistry();

    ArtifactRepository get(ArtifactRepositoryProperties props);
}
