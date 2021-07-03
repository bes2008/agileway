package com.jn.agileway.zip.archive;

import com.jn.langx.util.function.Consumer2;
import org.apache.commons.compress.archivers.ArchiveEntry;

import java.io.File;

public interface FileAttrsCopier extends Consumer2<ArchiveEntry, File> {
    @Override
    void accept(ArchiveEntry archiveEntry, File file);
}
