package com.jn.agileway.vfs.artifact.repository;

import com.jn.agileway.vfs.artifact.Artifact;
import com.jn.agileway.vfs.management.repository.FileRepositoryLayout;

public interface ArtifactRepositoryLayout extends FileRepositoryLayout<ArtifactRepository> {

    @Override
    String getFilePath(ArtifactRepository repository, String relativePath);

    @Override
    String getFileDigestPath(ArtifactRepository repository, String relativePath, String digest);

    String getPath(ArtifactRepository repository, Artifact artifact);

    String getDigitPath(ArtifactRepository repository, Artifact artifact, String digit);

    String toRelativePath(ArtifactRepository repository, Artifact artifact);

}
