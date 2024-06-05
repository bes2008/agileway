package com.jn.agileway.zip.archive.tar;

import com.jn.agileway.zip.archive.FileAttrsCopier;
import com.jn.langx.util.io.file.PosixFilePermissions;
import com.jn.langx.util.logging.Loggers;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;

import java.io.File;

public class TarFileEntryFileAttrsCopier implements FileAttrsCopier {
    @Override
    public void accept(ArchiveEntry archiveEntry, File file) {
        TarArchiveEntry entry = (TarArchiveEntry) archiveEntry;
        PosixFilePermissions permissions = new PosixFilePermissions(entry.getMode(), true, true);
        boolean actionResult= file.setWritable(permissions.isWritable());
        if(!actionResult){
            Loggers.getLogger(TarFileEntryFileAttrsCopier.class).warn("error when set writable, file: {}", file.getPath());
        }
        actionResult=file.setReadable(permissions.isReadable());
        if(!actionResult){
            Loggers.getLogger(TarFileEntryFileAttrsCopier.class).warn("error when set readable, file: {}", file.getPath());
        }
        actionResult=file.setExecutable(permissions.isExecutable());
        if(!actionResult){
            Loggers.getLogger(TarFileEntryFileAttrsCopier.class).warn("error when set executable, file: {}", file.getPath());
        }
    }
}
