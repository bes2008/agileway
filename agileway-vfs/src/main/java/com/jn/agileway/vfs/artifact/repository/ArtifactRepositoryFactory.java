package com.jn.agileway.vfs.artifact.repository;

import com.jn.langx.factory.Factory;
import com.jn.langx.registry.Registry;

public interface ArtifactRepositoryFactory extends Factory<ArtifactRepositoryProperties, ArtifactRepository> {
    void setArtifactRepositoryLayoutRegistry(Registry<String, ArtifactRepositoryLayout> registry);

    Registry<String, ArtifactRepositoryLayout> getRegistry();

    @Override
    ArtifactRepository get(ArtifactRepositoryProperties props);
}
