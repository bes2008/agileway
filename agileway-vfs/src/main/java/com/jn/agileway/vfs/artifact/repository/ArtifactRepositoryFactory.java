package com.jn.agileway.vfs.artifact.repository;

import com.jn.langx.factory.Factory;

public interface ArtifactRepositoryFactory extends Factory<ArtifactRepositoryProperties, ArtifactRepository> {
    @Override
    ArtifactRepository get(ArtifactRepositoryProperties props);
}
