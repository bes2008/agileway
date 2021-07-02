package com.jn.agileway.vfs.artifact;

import com.jn.agileway.vfs.VFSUtils;
import com.jn.agileway.vfs.VfsException;
import com.jn.agileway.vfs.artifact.repository.ArtifactRepository;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.struct.Holder;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.Selectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SynchronizedArtifactManager extends AbstractArtifactManager {
    private static final Logger logger = LoggerFactory.getLogger(SynchronizedArtifactManager.class);

    @Nullable
    private List<ArtifactRepository> sources;

    @NonNull
    private ArtifactRepository destination;


    public FileObject getArtifactFile(final Artifact artifact) {
        try {
            String localPath = destination.getPath(artifact);
            FileObject localFileObject = getFileSystemManager().resolveFile(localPath);
            if (!localFileObject.exists()) {
                if (Objs.isNotEmpty(sources)) {
                    final Holder<FileObject> remoteFileObjHolder = new Holder<FileObject>();
                    Collects.forEach(sources, new Predicate<ArtifactRepository>() {
                                @Override
                                public boolean test(ArtifactRepository repository) {
                                    return repository.isEnabled();
                                }
                            },
                            new Consumer<ArtifactRepository>() {
                                @Override
                                public void accept(ArtifactRepository repository) {
                                    String remotePath = repository.getPath(artifact);
                                    try {
                                        FileObject remoteFileObject = getFileSystemManager().resolveFile(remotePath);
                                        remoteFileObjHolder.set(remoteFileObject);
                                    } catch (Throwable ex) {
                                        logger.error(ex.getMessage(), ex);
                                    }
                                }
                            }, new Predicate<ArtifactRepository>() {
                                @Override
                                public boolean test(ArtifactRepository repository) {
                                    try {
                                        return VFSUtils.isExists(remoteFileObjHolder.get());
                                    } catch (VfsException ex) {
                                        logger.error(ex.getMessage(), ex);
                                        return false;
                                    }
                                }
                            });

                    if (!remoteFileObjHolder.isNull() && remoteFileObjHolder.get().exists()) {
                        if (remoteFileObjHolder.get().isFile()) {
                            localFileObject.copyFrom(remoteFileObjHolder.get(), Selectors.SELECT_SELF);
                        }
                    }
                }
            }
            return localFileObject;
        } catch (Throwable ex) {
            logger.error(ex.getMessage());
        }
        return null;
    }

    public void setSources(List<ArtifactRepository> sources) {
        this.sources = sources;
    }

    public void setDestination(ArtifactRepository dest) {
        this.destination = dest;
    }

    public void addSource(ArtifactRepository source) {
        this.sources.add(source);
    }

    @Override
    public List<ArtifactDigit> getDigits(final Artifact artifact) {
        return Pipeline.of(sources)
                .add(destination)
                .reverse(false)
                .filter(new Predicate<ArtifactRepository>() {
                    @Override
                    public boolean test(ArtifactRepository repository) {
                        return repository.isDigitSupports();
                    }
                })
                .map(new Function<ArtifactRepository, List<ArtifactDigit>>() {
                    @Override
                    public List<ArtifactDigit> apply(ArtifactRepository repository) {
                        return getDigits(repository, artifact);
                    }
                })
                .<ArtifactDigit>flat()
                .asList();
    }
}
