package com.jn.agileway.vfs.artifact.repository;

import com.jn.agileway.vfs.artifact.Artifact;
import com.jn.langx.Named;

public interface ArtifactRepositoryLayout extends Named {
    String getPath(ArtifactRepository repository, Artifact artifact);
    String getDigitPath(ArtifactRepository repository, Artifact artifact, String digit);
}
