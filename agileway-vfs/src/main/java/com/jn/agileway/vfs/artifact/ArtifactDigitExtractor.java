package com.jn.agileway.vfs.artifact;


import com.jn.langx.util.function.Function2;

public interface ArtifactDigitExtractor extends Function2<Artifact,String, String> {
    @Override
    String apply(Artifact artifact, String algorithm);
}
