package com.jn.agileway.vfs.artifact.repository;

import com.jn.agileway.vfs.artifact.Artifact;

public interface ArtifactRepository {
    String getId();
    void setId(String id);

    String getName();
    void setName(String name);

    String getUrl();
    void setUrl(String url);

    void setBasedir(String basedir);
    String getBasedir();
    String getProtocol();

    void setLayout(ArtifactRepositoryLayout layout);
    ArtifactRepositoryLayout getLayout();

    String getPath(Artifact artifact);
    boolean isEnabled();
}
