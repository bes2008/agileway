package com.jn.agileway.vfs.artifact;

import com.jn.agileway.vfs.artifact.repository.ArtifactRepository;
import com.jn.agileway.vfs.management.AbstractFileManager;
import com.jn.agileway.vfs.management.FileDigit;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;

import java.util.List;

public abstract class AbstractArtifactManager extends AbstractFileManager<ArtifactRepository, ArtifactDigitExtractor> implements ArtifactManager {

    @Override
    public void setRepository(ArtifactRepository repository) {
        super.setRepository(repository);
    }

    @Override
    public ArtifactRepository getRepository() {
        return super.getRepository();
    }

    @Override
    public ArtifactDigitExtractor getDigitExtractor() {
        return super.getDigitExtractor();
    }

    @Override
    public void setDigitExtractor(ArtifactDigitExtractor digitExtractor) {
        super.setDigitExtractor(digitExtractor);
    }

    protected List<FileDigit> getDigits(ArtifactRepository repository, final Artifact artifact) {
        if (repository.isDigitSupports()) {
            return Pipeline.of(repository.getSupportedDigits())
                    .map(new Function<String, FileDigit>() {
                        @Override
                        public FileDigit apply(String algorithm) {
                            String digit = getDigitExtractor().apply(artifact, algorithm);
                            if (Strings.isNotEmpty(digit)) {
                                return new FileDigit(algorithm, digit);
                            }
                            return null;
                        }
                    })
                    .clearNulls()
                    .asList();

        } else {
            return Collects.emptyArrayList();
        }

    }
}
