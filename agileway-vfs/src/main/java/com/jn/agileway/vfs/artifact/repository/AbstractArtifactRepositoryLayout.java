package com.jn.agileway.vfs.artifact.repository;

import com.jn.agileway.vfs.artifact.Artifact;
import com.jn.langx.AbstractNameable;
import com.jn.langx.util.Strings;

public abstract class AbstractArtifactRepositoryLayout extends AbstractNameable implements ArtifactRepositoryLayout {
    @Override
    public String getPath(ArtifactRepository repository, String relativePath) {
        String path = repository.getUrl();
        if (Strings.isNotEmpty(repository.getBasedir())) {
            path = addSegment(path, repository.getBasedir());
        }
        return path;
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
