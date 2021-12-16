package com.jn.agileway.vfs.artifact;


import com.jn.agileway.vfs.management.FileDigestExtractor;

public interface ArtifactDigitExtractor extends FileDigestExtractor<ArtifactManager> {
    @Override
    String apply(String relativePath, String algorithm);

    String apply(Artifact artifact, String algorithm);
}
