package com.jn.agileway.vfs.artifact.repository;

import com.jn.agileway.vfs.artifact.Artifact;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;

public class LocalArtifactRepositoryLayout implements ArtifactRepositoryLayout {

    @Override
    public String getPath(ArtifactRepository repository, String relativePath) {
        String path = repository.getUrl();
        if (Strings.isNotEmpty(repository.getBasedir())) {
            path = addSegment(path, repository.getBasedir());
        }
        return path;
    }

    @Override
    public String getPath(ArtifactRepository repository, Artifact artifact) {
        String relativePath = "";
        relativePath = addSegment(relativePath, artifact.getArtifactId());
        if (Strings.isNotEmpty(artifact.getVersion())) {
            relativePath = addSegment(relativePath, artifact.getVersion());
            relativePath = addSegment(relativePath, artifact.getArtifactId() + "-" + artifact.getVersion() + (Objs.isEmpty(artifact.getClassifier()) ? "" : ("." + artifact.getClassifier())) + "." + artifact.getExtension());
        }
        return getPath(repository, relativePath);
    }

    private String addSegment(String path, String segment) {
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

    @Override
    public void setName(String s) {

    }

    @Override
    public String getName() {
        return "local";
    }
}
