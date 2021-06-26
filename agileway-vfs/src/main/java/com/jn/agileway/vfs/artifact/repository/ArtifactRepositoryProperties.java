package com.jn.agileway.vfs.artifact.repository;

import java.util.List;

public class ArtifactRepositoryProperties {
    private String name;
    private String url;
    private String basedir;
    private boolean enabled;
    private String layout;
    private List<String> supportedDigits;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBasedir() {
        return basedir;
    }

    public void setBasedir(String basedir) {
        this.basedir = basedir;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public List<String> getSupportedDigits() {
        return supportedDigits;
    }

    public void setSupportedDigits(List<String> supportedDigits) {
        this.supportedDigits = supportedDigits;
    }
}
