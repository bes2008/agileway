package com.jn.agileway.zip.archive.tar;

import com.jn.agileway.zip.archive.FileAttrsCopier;
import com.jn.langx.util.io.file.PosixFilePermissions;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;

import java.io.File;

public class TarFileEntryFileAttrsCopier implements FileAttrsCopier {
    @Override
    public void accept(ArchiveEntry archiveEntry, File file) {
        TarArchiveEntry entry = (TarArchiveEntry) archiveEntry;
        PosixFilePermissions permissions = new PosixFilePermissions(entry.getMode(), true, true);
        file.setWritable(permissions.isWritable());
        file.setReadable(permissions.isReadable());
        file.setExecutable(permissions.isExecutable());
    }
}
