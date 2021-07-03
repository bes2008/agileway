package com.jn.agileway.compress.archive;

import com.jn.langx.util.function.Consumer2;
import org.apache.commons.compress.archivers.ArchiveEntry;

import java.io.File;

public interface FileAttrCopier extends Consumer2<ArchiveEntry, File> {
    @Override
    void accept(ArchiveEntry archiveEntry, File file);
}
