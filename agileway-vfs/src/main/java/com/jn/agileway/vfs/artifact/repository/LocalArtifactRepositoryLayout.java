package com.jn.agileway.vfs.artifact.repository;

import com.jn.agileway.vfs.artifact.Artifact;

public class LocalArtifactRepositoryLayout implements ArtifactRepositoryLayout {
    @Override
    public String getPath(ArtifactRepository repository, Artifact artifact) {
        return repository.getUrl() + repository.getBasedir() + artifact.getArtifactId() + artifact.getClassifier() + artifact.getExtension();
    }

    @Override
    public void setName(String s) {

    }

    @Override
    public String getName() {
        return "local";
    }
}
