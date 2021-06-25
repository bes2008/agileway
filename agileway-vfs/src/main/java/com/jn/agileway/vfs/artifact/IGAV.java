package com.jn.agileway.vfs.artifact;

public interface IGAV {
    String getGroupId();

    String getArtifactId();

    String getVersion();

    void setGroupId(String groupId);

    void setArtifactId(String artifactId);

    void setVersion(String version);
}
