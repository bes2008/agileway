package com.jn.agileway.vfs.artifact;

import com.jn.agileway.vfs.artifact.repository.ArtifactRepositoryProperties;

import java.util.List;

public class ArtifactManagerProperties {
    private boolean enabled = false;
    private List<ArtifactRepositoryProperties> repositories;

    public List<ArtifactRepositoryProperties> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<ArtifactRepositoryProperties> repositories) {
        this.repositories = repositories;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
