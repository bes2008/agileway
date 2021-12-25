package com.jn.agileway.vfs.management;

import com.jn.agileway.vfs.VfsException;
import com.jn.agileway.vfs.management.repository.FileRepository;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;

import java.util.List;

public abstract class AbstractFileManager<R extends FileRepository, E extends FileDigestExtractor> implements FileManager<R> {
    private FileSystemManager fileSystemManager;
    private E digitExtractor;
    private R repository;

    @Override
    public FileSystemManager getFileSystemManager() {
        return fileSystemManager;
    }

    @Override
    public void setFileSystemManager(FileSystemManager manager) {
        this.fileSystemManager = manager;
    }

    public E getDigitExtractor() {
        return digitExtractor;
    }

    public void setDigitExtractor(E digitExtractor) {
        this.digitExtractor = digitExtractor;
        this.digitExtractor.setManager(this);
    }

    @Override
    public FileObject getFile(String relativePath) {
        try {
            return fileSystemManager.resolveFile(repository.getFilePath(relativePath));
        } catch (Throwable ex) {
            throw new VfsException(ex);
        }
    }

    @Override
    public List<FileDigit> getDigits(final String relativePath) {
        if (repository.isDigitSupports()) {
            return getDigits(repository, relativePath);

        } else {
            return Collects.emptyArrayList();
        }
    }

    protected List<FileDigit> getDigits(R repository, final String relativePath) {
        if (repository.isDigitSupports()) {
            return Pipeline.of(repository.getSupportedDigits())
                    .map(new Function<String, FileDigit>() {
                        @Override
                        public FileDigit apply(String algorithm) {
                            String digit = getDigitExtractor().apply(relativePath, algorithm);
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

    @Override
    public void setRepository(R repository) {
        this.repository = repository;
    }

    @Override
    public R getRepository() {
        return this.repository;
    }
}
