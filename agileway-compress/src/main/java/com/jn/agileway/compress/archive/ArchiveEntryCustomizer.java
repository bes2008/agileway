package com.jn.agileway.compress.archive;

import com.jn.langx.Customizer;
import org.apache.commons.compress.archivers.ArchiveEntry;

public interface ArchiveEntryCustomizer extends Customizer<ArchiveEntry> {
    @Override
    void customize(ArchiveEntry entry);
}
