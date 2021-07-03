package com.jn.agileway.compress.archive;

import com.jn.langx.Filter;
import org.apache.commons.compress.archivers.ArchiveEntry;

public interface ArchiveEntryFilter extends Filter<ArchiveEntry> {
    @Override
    boolean accept(ArchiveEntry archiveEntry);
}
