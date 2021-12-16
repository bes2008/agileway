package com.jn.agileway.vfs.management.repository;

public interface FileRepositoryAware<R extends FileRepository> {
    void setRepository(R repository);
    R getRepository();
}
