package com.jn.agileway.vfs.management;

import com.jn.agileway.vfs.FileSystemManagerAware;
import com.jn.agileway.vfs.management.repository.FileRepository;
import com.jn.agileway.vfs.management.repository.FileRepositoryAware;
import org.apache.commons.vfs2.FileObject;

import java.util.List;

public interface FileManager<R extends FileRepository> extends FileSystemManagerAware, FileRepositoryAware<R> {
    FileObject getFile(String relativePath);

    /**
     * @param relativePath
     * @return
     */
    List<FileDigit> getDigits(String relativePath);
}
