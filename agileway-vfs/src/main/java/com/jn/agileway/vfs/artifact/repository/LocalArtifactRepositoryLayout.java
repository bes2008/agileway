package com.jn.agileway.vfs.artifact.repository;

import com.jn.agileway.vfs.artifact.Artifact;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;

public class LocalArtifactRepositoryLayout extends AbstractArtifactRepositoryLayout {

    public LocalArtifactRepositoryLayout() {
        setName("local");
    }


    @Override
    public String toRelativePath(ArtifactRepository repository, Artifact artifact) {
        String relativePath = "";
        relativePath = addSegment(relativePath, artifact.getArtifactId());
        if (Strings.isNotEmpty(artifact.getVersion())) {
            relativePath = addSegment(relativePath, artifact.getVersion());
            relativePath = addSegment(relativePath, artifact.getArtifactId() + "-" + artifact.getVersion() + (Objs.isEmpty(artifact.getClassifier()) ? "" : ("." + artifact.getClassifier())) + "." + artifact.getExtension());
        }
        return relativePath;
    }
}
