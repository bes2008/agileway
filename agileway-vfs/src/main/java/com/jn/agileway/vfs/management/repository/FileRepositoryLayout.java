package com.jn.agileway.vfs.management.repository;

import com.jn.langx.Nameable;

public interface FileRepositoryLayout<R extends FileRepository> extends Nameable {
    String getFilePath(R repository, String relativePath);

    String getFileDigestPath(R repository, String relativePath, String digest);
}
