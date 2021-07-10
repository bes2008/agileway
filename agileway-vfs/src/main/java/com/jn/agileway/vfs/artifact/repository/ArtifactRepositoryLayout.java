package com.jn.agileway.vfs.artifact.repository;

import com.jn.agileway.vfs.artifact.Artifact;
import com.jn.langx.Nameable;

public interface ArtifactRepositoryLayout extends Nameable {
    String getPath(ArtifactRepository repository, Artifact artifact);
    String getDigitPath(ArtifactRepository repository, Artifact artifact, String digit);
}
