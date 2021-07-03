package com.jn.agileway.zip.archive;

import com.jn.langx.Customizer;
import org.apache.commons.compress.archivers.ArchiveOutputStream;

public interface ArchiveOutputStreamCustomizer extends Customizer<ArchiveOutputStream> {
    @Override
    void customize(ArchiveOutputStream archiveOutputStream);
}
