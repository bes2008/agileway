package com.jn.agileway.vfs.artifact;

import com.jn.agileway.vfs.artifact.repository.ArtifactRepositoryProperties;

import java.util.List;

public class ArtifactProperties {
    private List<ArtifactRepositoryProperties> repositories;

    public List<ArtifactRepositoryProperties> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<ArtifactRepositoryProperties> repositories) {
        this.repositories = repositories;
    }
}
