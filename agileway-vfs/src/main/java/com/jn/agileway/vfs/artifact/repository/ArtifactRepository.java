package com.jn.agileway.vfs.artifact.repository;

import com.jn.agileway.vfs.artifact.Artifact;

public interface ArtifactRepository {
    String getId();
    void setId(String id);

    String getUrl();
    void setUrl(String url);

    String getBasedir();
    String getProtocol();

    ArtifactRepositoryLayout getLayout();

    String getPath(Artifact artifact);
    boolean isEnabled();
}
