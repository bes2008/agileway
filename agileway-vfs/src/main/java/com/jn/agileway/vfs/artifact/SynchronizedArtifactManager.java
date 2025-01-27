package com.jn.agileway.vfs.artifact;

import com.jn.agileway.vfs.VfsException;
import com.jn.agileway.vfs.artifact.repository.ArtifactRepository;
import com.jn.agileway.vfs.management.FileDigit;
import com.jn.agileway.vfs.utils.FileObjects;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.struct.Holder;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.Selectors;
import org.slf4j.Logger;

import java.util.List;

public class SynchronizedArtifactManager extends AbstractArtifactManager {
    private static final Logger logger = Loggers.getLogger(SynchronizedArtifactManager.class);

    @Nullable
    private List<ArtifactRepository> sources = Collects.emptyArrayList();


    public FileObject getArtifactFile(final Artifact artifact) {
        FileObject localFileObject = null;
        try {
            String localPath = getRepository().getPath(artifact);
            localFileObject = getFileSystemManager().resolveFile(localPath);
            if (!FileObjects.isExists(localFileObject) && artifact.isSupportSynchronized()) {
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
                                        return FileObjects.isExists(remoteFileObjHolder.get());
                                    } catch (VfsException ex) {
                                        logger.error(ex.getMessage(), ex);
                                        return false;
                                    }
                                }
                            });

                    if (FileObjects.isExists(remoteFileObjHolder.get())) {
                        if (remoteFileObjHolder.get().isFile()) {
                            logger.info("sync {} ==> {}", remoteFileObjHolder.get().getName().getURI(), localFileObject.getName().getURI());
                            localFileObject.copyFrom(remoteFileObjHolder.get(), Selectors.SELECT_SELF);
                        }
                    }
                }
            }
            return localFileObject;
        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
        }
        return localFileObject;
    }

    public void setSources(List<ArtifactRepository> sources) {
        this.sources = sources;
    }

    public void setDestination(ArtifactRepository dest) {
        this.setRepository(dest);
    }

    public void addSource(ArtifactRepository source) {
        this.sources.add(source);
    }

    @Override
    public List<FileDigit> getDigits(final Artifact artifact) {
        return Pipeline.of(sources)
                .add(getDestination())
                .reverse(false)
                .filter(new Predicate<ArtifactRepository>() {
                    @Override
                    public boolean test(ArtifactRepository repository) {
                        return repository.isDigitSupports();
                    }
                })
                .map(new Function<ArtifactRepository, List<FileDigit>>() {
                    @Override
                    public List<FileDigit> apply(ArtifactRepository repository) {
                        return getDigits(repository, artifact);
                    }
                })
                .<FileDigit>flat()
                .asList();
    }

    public List<ArtifactRepository> getSources() {
        return sources;
    }

    public ArtifactRepository getDestination() {
        return getRepository();
    }
}
