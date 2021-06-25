package com.jn.agileway.vfs.artifact;

public interface IGAV {
    String getGroupId();

    String getArtifactId();

    String getVersion();

    void setGroup(String groupId);

    void setArtifactId(String artifactId);

    void setVersion(String version);
}
