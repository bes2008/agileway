package com.jn.agileway.vfs.artifact;

import com.jn.agileway.vfs.artifact.repository.ArtifactRepository;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import org.apache.commons.vfs2.FileSystemManager;

import java.util.List;

public abstract class AbstractArtifactManager implements ArtifactManager {
    private FileSystemManager fileSystemManager;
    private ArtifactDigitExtractor digitExtractor;
    private ArtifactRepository repository;

    @Override
    public ArtifactRepository getRepository() {
        return repository;
    }

    @Override
    public void setRepository(ArtifactRepository repository) {
        this.repository = repository;
    }

    @Override
    public FileSystemManager getFileSystemManager() {
        return fileSystemManager;
    }

    @Override
    public void setFileSystemManager(FileSystemManager fileSystemManager) {
        this.fileSystemManager = fileSystemManager;
    }

    public ArtifactDigitExtractor getDigitExtractor() {
        return digitExtractor;
    }

    public void setDigitExtractor(ArtifactDigitExtractor digitExtractor) {
        this.digitExtractor = digitExtractor;
    }

    protected List<ArtifactDigit> getDigits(ArtifactRepository repository, final Artifact artifact) {
        if (repository.isDigitSupports()) {
            return Pipeline.of(repository.getSupportedDigits())
                    .map(new Function<String, ArtifactDigit>() {
                        @Override
                        public ArtifactDigit apply(String algorithm) {
                            String digit = getDigitExtractor().apply(artifact, algorithm);
                            if (Strings.isNotEmpty(digit)) {
                                return new ArtifactDigit(algorithm, digit);
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
