package com.jn.agileway.vfs.artifact.repository;

import com.jn.agileway.vfs.artifact.Artifact;
import com.jn.agileway.vfs.management.repository.DefaultFileRepository;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;

public class DefaultArtifactRepository extends DefaultFileRepository<ArtifactRepositoryLayout> implements ArtifactRepository {

    @Override
    public String getPath(Artifact artifact) {
        return getLayout().getPath(this, artifact);
    }

    @Override
    public String getDigitPath(Artifact artifact, String digit) {
        Preconditions.checkTrue(Collects.contains(getSupportedDigits(), digit));
        return getLayout().getDigitPath(this, artifact, digit);
    }


}
