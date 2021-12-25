package com.jn.agileway.vfs.management;

import com.jn.langx.util.function.Function2;

public interface FileDigestExtractor<M extends FileManager> extends Function2<String, String, String>, FileManagerAware<M> {
    @Override
    String apply(String relativePath, String algorithm);
}
