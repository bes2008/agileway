package com.jn.agileway.vfs.artifact;

import com.jn.langx.factory.Factory;

public interface ArtifactFactory<GAV extends IGAV, A extends Artifact> extends Factory<GAV,A> {
    @Override
    A get(GAV input);
}
