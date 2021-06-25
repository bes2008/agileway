package com.jn.agileway.vfs.artifact.repository;

import com.jn.agileway.vfs.artifact.Artifact;
import com.jn.langx.util.Strings;
import com.jn.langx.util.Throwables;

import java.net.URL;

public class DefaultArtifactRepository implements ArtifactRepository {
    private String id;
    private String name;
    private String url;
    private String protocol;
    private String basedir;
    private ArtifactRepositoryLayout layout;
    private boolean isEnabled = false;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public void setUrl(String url) {
        if (Strings.isNotEmpty(url)) {
            try {
                URL u = new URL(url);
                this.protocol = u.getProtocol();
                this.url = url;
            } catch (Throwable ex) {
                throw Throwables.wrapAsRuntimeException(ex);
            }
        }
    }

    @Override
    public String getBasedir() {
        return this.basedir;
    }

    @Override
    public void setBasedir(String basedir) {
        this.basedir = basedir;
    }

    @Override
    public String getProtocol() {
        return this.protocol;
    }

    @Override
    public void setLayout(ArtifactRepositoryLayout layout) {
        this.layout = layout;
    }

    @Override
    public ArtifactRepositoryLayout getLayout() {
        return layout;
    }

    @Override
    public String getPath(Artifact artifact) {
        return layout.getPath(this, artifact);
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
