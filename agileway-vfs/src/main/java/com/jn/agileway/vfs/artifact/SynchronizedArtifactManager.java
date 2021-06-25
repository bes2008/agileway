package com.jn.agileway.vfs.artifact;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.struct.Holder;
import com.jn.agileway.vfs.artifact.repository.ArtifactRepository;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.Selectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SynchronizedArtifactManager extends AbstractArtifactManager {
    private static final Logger logger = LoggerFactory.getLogger(SynchronizedArtifactManager.class);

    @Nullable
    private List<ArtifactRepository> srcArtifactRepositories;

    @NonNull
    private ArtifactRepository destArtifactRepository;


    public FileObject getArtifactFile(final Artifact artifact) {
        try {
            String localPath = destArtifactRepository.getPath(artifact);
            FileObject localFileObject = getFileSystemManager().resolveFile(localPath);
            if (!localFileObject.exists()) {
                if (Objs.isNotEmpty(srcArtifactRepositories)) {
                    final Holder<FileObject> remoteFileObjHolder = new Holder<FileObject>();
                    Collects.forEach(srcArtifactRepositories, new Predicate<ArtifactRepository>() {
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

                                    }
                                }
                            }, new Predicate<ArtifactRepository>() {
                                @Override
                                public boolean test(ArtifactRepository repository) {
                                    try {
                                        return !remoteFileObjHolder.isNull() && remoteFileObjHolder.get().exists();
                                    } catch (Throwable ex) {

                                    }
                                    return false;
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

    public void setSrcArtifactRepositories(List<ArtifactRepository> srcArtifactRepositories) {
        this.srcArtifactRepositories = srcArtifactRepositories;
    }

    public void setDestArtifactRepository(ArtifactRepository destArtifactRepository) {
        this.destArtifactRepository = destArtifactRepository;
    }

    public void addSourceArtifactRepository(ArtifactRepository remote) {
        this.srcArtifactRepositories.add(remote);
    }
}
