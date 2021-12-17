package com.jn.agileway.vfs.management.repository;

import com.jn.langx.AbstractNameable;
import com.jn.langx.util.Strings;

public abstract class AbstractFileRepositoryLayout<R extends FileRepository> extends AbstractNameable implements FileRepositoryLayout<R> {
    @Override
    public String getFilePath(R repository, String relativePath) {
        String path = repository.getUrl();
        if (Strings.isNotEmpty(repository.getBasedir())) {
            path = addSegment(path, repository.getBasedir());
        }
        return path;
    }

    @Override
    public String getFileDigestPath(R repository, String relativePath, String digest) {
        return getFilePath(repository, relativePath) + "." + digest.toLowerCase();
    }


    protected String addSegment(String path, String segment) {
        if (Strings.isNotEmpty(segment)) {
            segment = Strings.strip(segment, "/");
        }
        path = Strings.stripEnd(path, "/");
        return Strings.isEmpty(segment) ? path : (path + "/" + segment);
    }

}
