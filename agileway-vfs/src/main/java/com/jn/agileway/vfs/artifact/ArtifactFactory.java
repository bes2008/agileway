package com.jn.agileway.vfs.artifact;


import com.jn.langx.Factory;

public interface ArtifactFactory<GAV extends IGAV, A extends Artifact> extends Factory<GAV, A> {
    @Override
    A get(GAV gav);
}
