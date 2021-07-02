package com.jn.agileway.vfs.artifact.repository;

import com.jn.agileway.vfs.artifact.Artifact;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;

public class LocalArtifactRepositoryLayout implements ArtifactRepositoryLayout {
    @Override
    public String getPath(ArtifactRepository repository, Artifact artifact) {
        String path = repository.getUrl();
        path = addSegment(path, repository.getBasedir());
        path = addSegment(path, artifact.getArtifactId());
        path = addSegment(path, artifact.getVersion());
        path = addSegment(path, artifact.getArtifactId() + "-" + artifact.getVersion() + (Objs.isEmpty(artifact.getClassifier()) ? "" : ("." + artifact.getClassifier())) + "." + artifact.getExtension());
        return path;
    }

    private String addSegment(String path, String segment) {

        while (Strings.endsWith(segment, "/")) {
            segment = segment.substring(0, segment.length() - 1);
        }

        while (Strings.startsWith(segment, "/")) {
            segment = segment.substring(1);
        }

        while (Strings.endsWith(path, "/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path + "/" + segment;
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
