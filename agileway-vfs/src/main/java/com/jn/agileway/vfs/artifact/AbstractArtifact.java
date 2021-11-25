package com.jn.agileway.vfs.artifact;

public abstract class AbstractArtifact implements Artifact{
    private String groupId;
    private String artifactId;
    private String version;
    private String classifier;
    private String extension;
    private boolean supportSynchronized = false;

    @Override
    public String getGroupId() {
        return groupId;
    }

    @Override
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public String getArtifactId() {
        return artifactId;
    }

    @Override
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String getClassifier() {
        return classifier;
    }

    @Override
    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    @Override
    public String getExtension() {
        return extension;
    }

    @Override
    public void setExtension(String extension) {
        this.extension = extension;
    }

    public boolean isSupportSynchronized() {
        return supportSynchronized;
    }

    public void setSupportSynchronized(boolean supportSynchronized) {
        this.supportSynchronized = supportSynchronized;
    }
}
