package com.jn.agileway.vfs.artifact.repository;

import com.jn.agileway.vfs.artifact.Artifact;
import com.jn.agileway.vfs.management.repository.AbstractFileRepositoryLayout;
import com.jn.langx.util.Strings;

public abstract class AbstractArtifactRepositoryLayout extends AbstractFileRepositoryLayout<ArtifactRepository> implements ArtifactRepositoryLayout {
    @Override
    public String getFilePath(ArtifactRepository repository, String relativePath) {
        String path = repository.getUrl();
        if (Strings.isNotEmpty(repository.getBasedir())) {
            path = addSegment(path, repository.getBasedir());
        }
        if(Strings.isNotEmpty(relativePath)){
            path = addSegment(path, relativePath);
        }
        return path;
    }

    @Override
    public String getPath(ArtifactRepository repository, Artifact artifact) {
        String relativePath = toRelativePath(repository, artifact);
        return getFilePath(repository, relativePath);
    }

    protected String addSegment(String path, String segment) {
        if (Strings.isNotEmpty(segment)) {
            segment = Strings.strip(segment, "/");
        }
        path = Strings.stripEnd(path, "/");
        return Strings.isEmpty(segment) ? path : (path + "/" + segment);
    }

    @Override
    public String getDigitPath(ArtifactRepository repository, Artifact artifact, String digit) {
        String artifactPath = getPath(repository, artifact);
        return artifactPath + "." + digit.toLowerCase();
    }

}
